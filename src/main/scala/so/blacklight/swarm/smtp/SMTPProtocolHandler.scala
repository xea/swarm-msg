package so.blacklight.swarm.smtp

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging

/**
	* Impelments the SMTP protocol by supervising the lower level client session and deciding
	* what replies to send to the clients' requests.
	*
	* @param clientSession client session
	*/
class SMTPProtocolHandler(clientSession: ActorRef) extends Actor {

	import context._

	val logger = Logging(context.system, this)

	/**
		* This is the initial stage of mail processing, switches to "expect EHLO" mode immediately
		* after issuing the server greeting
		*/
	override def receive: Receive = {
		case greeting @ SMTPServerGreeting(_) =>
			clientSession ! greeting
			become(expectEhlo)
	}

	def expectEhlo: PartialFunction[Any, Unit] = {
		case SMTPClientEhlo(hostId) =>
			sender() ! processEhlo(hostId)
			become(expectEmail)

		case SMTPClientQuit =>
			sender() ! SMTPServerQuit

		case ClientDisconnected =>
			logger.warning("Client disconnected unexpectedly")

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
			sender() ! processDataSent(msg)

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
	}

	private def processEhlo(hostname: String): SMTPServerEvent = {
		logger.info(s"Received client connection from: $hostname")
		SMTPServerEhlo(Array("THIS", "THAT"))
	}

	private def processMailFrom(sender: String): SMTPServerEvent = {
		logger.info(s"Sender: $sender")
		SMTPServerOk
	}

	private def processReceiptTo(recipient: String): SMTPServerEvent = {
		logger.info(s"Recipient: $recipient")
		SMTPServerOk
	}

	private def processDataRequest: SMTPServerEvent = {
		logger.info("Received DATA request")
		SMTPServerDataOk
	}

	private def processDataSent(msg: Array[Byte]): SMTPServerEvent = {
		logger.info(s"Received SMTP message, size: ${msg.length} bytes")
		SMTPServerOk
	}
}

object SMTPProtocolHandler {

  def props(clientSession: ActorRef): Props = Props(new SMTPProtocolHandler(clientSession))

}
