package so.blacklight.swarm.smtp

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import so.blacklight.swarm.mail.{Address, Email, Envelope}

/**
	* Impelments the SMTP protocol by supervising the lower level client session and deciding
	* what replies to send to the clients' requests.
	*
	* @param clientSession client session
	*/
class SMTPServerProtocol(clientSession: ActorRef, connector: ActorRef) extends Actor {

	import context._

	val logger = Logging(context.system, this)

	var tempEnvelope = new PartialEnvelope

	/**
		* This is the initial stage of mail processing, switches to "expect EHLO" mode immediately
		* after issuing the server greeting
		*/
	override def receive: Receive = {
		case greeting @ SMTPServerServiceReady(_) =>
			clientSession ! greeting
			become(expectEhlo)
		case ClientDisconnected =>
			logger.warning("Client disconnected unexpectedly")
			sender() ! ClientDisconnected
	}

	def expectEhlo: PartialFunction[Any, Unit] = {
		case SMTPClientEhlo(hostId) =>
			sender() ! processEhlo(hostId)
			become(expectEmail)

		case SMTPClientQuit =>
			sender() ! SMTPServerQuit

		case SMTPClientNoOperation =>
			sender() ! SMTPServerOk

		case ClientDisconnected =>
			logger.warning("Client disconnected unexpectedly")
			sender() ! ClientDisconnected
			unbecome()

		case _: SMTPClientCommand =>
			sender() ! SMTPServerBadSequence

		case unknownMessage =>
			logger.warning(s"Received unknown event: $unknownMessage")
			sender() ! SMTPServerSyntaxError
	}

	def expectEmail: PartialFunction[Any, Unit] = {
		case SMTPClientMailFrom(mailFrom) =>
			sender() ! processMailFrom(mailFrom)

		case SMTPClientReceiptTo(recipient) =>
			sender() ! processReceiptTo(recipient)

		case SMTPClientDataBegin =>
			sender() ! processDataRequest

		case SMTPClientDataEnd(msg) =>
			sender() ! processDataSent(msg)

		case SMTPClientReset =>
			sender() ! processReset
			unbecome()

		case SMTPClientQuit =>
			sender() ! SMTPServerQuit

		case ClientDisconnected =>
			logger.warning("Client disconnected unexpectedly")
			sender() ! ClientDisconnected
			unbecome()

		case unknownMessage =>
			logger.warning(s"Received unknown event: $unknownMessage")
			sender() ! SMTPServerSyntaxError
	}

	private def processEhlo(hostname: String): SMTPServerEvent = {
		logger.info(s"Received client connection from: $hostname")
		SMTPServerEhlo(Array("THIS", "THAT"))
	}

	private def processMailFrom(sender: String): SMTPServerEvent = {
		Address(sender) match {
			case Left(error) =>
				logger.warning(s"Invalid sender address: $error")
				SMTPServerInvalidParameter

			case Right(address) =>
				tempEnvelope.sender match {
					case Some(_) =>
						logger.error("A sender address has already been defined")
						SMTPServerBadSequence

					case None =>
						tempEnvelope.setSender(address)
						logger.info(s"Sender: $address")
						SMTPServerOk
				}
		}
	}

	private def processReceiptTo(recipient: String): SMTPServerEvent = {
		Address(recipient) match {
			case Left(error) =>
				logger.warning(s"Invalid recipient address: $error")
				SMTPServerInvalidParameter

			case Right(address) =>
				tempEnvelope.sender match {
					case Some(_) =>
						tempEnvelope.addRecipient(address)
						logger.info(s"Recipient: $address")
						SMTPServerOk

					case None =>
						logger.error("Invalid command sequence")
						SMTPServerBadSequence
				}
		}
	}

	private def processDataRequest: SMTPServerEvent = {
		if (tempEnvelope.isComplete()) {
			logger.info("Received DATA request")
			SMTPServerDataReady
		} else {
			SMTPServerBadSequence
		}
	}

	private def processDataSent(msg: Array[Char]): SMTPServerEvent = {
		tempEnvelope.toEnvelope() match {
			case Right(envelope) =>
				Email(envelope, msg) match {
					case Left(_) =>
						// TODO find a better error, although this is not expected to happen too often
						SMTPServerSyntaxError
					case Right(email) =>
						connector ! ReceivedMessage(email)
						SMTPServerOk
				}
			case Left(error) =>
				logger.error(error)
				SMTPServerBadSequence
		}
	}

	private def processReset: SMTPServerEvent = {
		tempEnvelope.reset

		SMTPServerOk
	}
}

object SMTPServerProtocol {

  def props(clientSession: ActorRef, connector: ActorRef): Props = Props(new SMTPServerProtocol(clientSession, connector))

}

class PartialEnvelope {
	var sender: Option[Address] = None

	var recipients: List[Address] = List()

	def reset: PartialEnvelope = {
		sender = None
		recipients = List()
		this
	}

	def setSender(sender: Address): PartialEnvelope = {
		this.sender = Option(sender)
		this
	}

	def addRecipient(recipient: Address): PartialEnvelope = {
		recipients ++= List(recipient)
		this
	}

	def toEnvelope(): Either[String, Envelope] = {
		sender.map(address => Right(Envelope(address, recipients))).getOrElse(Left("Invalid envelope: sender missing"))
	}

	def hasSender(): Boolean = sender.isDefined

	def hasRecipient(): Boolean = recipients.nonEmpty

	def isComplete(): Boolean = hasSender() && hasRecipient()
}
