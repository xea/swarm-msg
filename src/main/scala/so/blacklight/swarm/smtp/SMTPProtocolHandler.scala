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
class SMTPProtocolHandler(clientSession: ActorRef, connector: ActorRef) extends Actor {

	import context._

	val logger = Logging(context.system, this)

	var tempEnvelope = new PartialEnvelope

	/**
		* This is the initial stage of mail processing, switches to "expect EHLO" mode immediately
		* after issuing the server greeting
		*/
	override def receive: Receive = {
		case greeting @ SMTPServerGreeting(_) =>
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

		case ClientDisconnected =>
			logger.warning("Client disconnected unexpectedly")
			sender() ! ClientDisconnected
			unbecome()

		case unknownMessage =>
			logger.warning(s"Received unknown event: $unknownMessage")
	}

	def expectEmail: PartialFunction[Any, Unit] = {
		case SMTPClientMailFrom(mailFrom) =>
			sender() ! processMailFrom(mailFrom)

		case SMTPClientReceiptTo(recipient) =>
			sender() ! processReceiptTo(recipient)

		case SMTPClientDataBegin =>
			sender() ! processDataRequest

		case SMTPClientDataEnd(msg) =>
			tempEnvelope.toEnvelope() match {
				case Right(envelope) =>
					Email(envelope, msg) match {
						case Left(error) =>
							logger.error(s"$error")
							// TODO instead of processDataSent there should be an error notification
							sender() ! processDataSent(msg)
						case Right(email) =>
							connector ! ReceivedMessage(email)
							sender() ! processDataSent(msg)
					}
				case Left(error) =>
					logger.error(s"$error")
					// TODO finish error handling
			}
		case SMTPClientReset =>
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
			case Left(error) => logger.warning(s"Invalid sender address: $error")
			case Right(address) =>
				tempEnvelope.sender match {
					case Some(_) => logger.error("A sender address has already been defined")
					case None =>
						tempEnvelope.setSender(address)
						logger.info(s"Sender: $address")
				}
		}

		SMTPServerOk
	}

	private def processReceiptTo(recipient: String): SMTPServerEvent = {
		Address(recipient) match {
			case Left(error) => logger.warning(s"Invalid recipient address: $error")
			case Right(address) =>
				tempEnvelope.addRecipient(address)
				logger.info(s"Recipient: $address")
		}

		SMTPServerOk
	}

	private def processDataRequest: SMTPServerEvent = {
		logger.info("Received DATA request")
		SMTPServerDataReady
	}

	private def processDataSent(msg: Array[Char]): SMTPServerEvent = {
		logger.info(s"Received SMTP message, size: ${msg.length} bytes")
		SMTPServerDataOk
	}
}

object SMTPProtocolHandler {

  def props(clientSession: ActorRef, connector: ActorRef): Props = Props(new SMTPProtocolHandler(clientSession, connector))

}

class PartialEnvelope {
	var sender: Option[Address] = None

	var recipients: List[Address] = List()

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

	def hasRecipient(): Boolean = !recipients.isEmpty

	def isComplete(): Boolean = hasSender() && hasRecipient()
}
