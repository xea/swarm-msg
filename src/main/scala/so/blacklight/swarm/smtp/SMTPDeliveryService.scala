package so.blacklight.swarm.smtp

import java.net.Socket

import akka.actor.{Actor, Props}
import akka.event.Logging
import so.blacklight.swarm.mail.Email

/**
	*
	*/
class SMTPDeliveryService extends Actor {

	val logger = Logging(context.system, this)

	override def preStart(): Unit = {
		logger.info("SMTP Delivery service started up")
	}

	override def receive: Receive = {
		case DeliverMessage(email) =>
			logger.info("Begin message delivery")
			deliverMessages(Stream(email))
		case _ => ()
	}

	private def deliverMessages(messages: Stream[Email]): Unit = {
		val deliverySession = context.actorOf(SMTPDeliverySession.props)

		deliverySession ! DeliverMessageStream(messages)
	}
}

object SMTPDeliveryService {
	def props: Props = Props(new SMTPDeliveryService)
}

case class DeliveryConfig(remoteHost: String, remotePort: Int, forceTLS: Boolean)

class SMTPDeliverySession extends Actor {

	val logger = Logging(context.system, this)

	override def receive: Receive = {
		case DeliverMessageStream(messageStream) => deliverStream(messageStream)
		case _ =>
	}

	private def deliverStream(messageStream: Stream[Email]): Unit = {
		try {
			val socket = new Socket("localhost", 1025)

			val clientSession = context.actorOf(SMTPClientSession.props(socket, SessionID()))
			val clientProtocol = context.actorOf(SMTPClientProtocol.props(clientSession, self, messageStream))

			clientProtocol ! InitTransaction

		} catch {
			case ex: Exception =>
				logger.error(s"Error during delivery: ${ex.getMessage}")
		}
	}
}

object SMTPDeliverySession {
	def props: Props = Props(new SMTPDeliverySession)
}
