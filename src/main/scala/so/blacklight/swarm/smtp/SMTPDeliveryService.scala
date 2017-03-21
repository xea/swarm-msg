package so.blacklight.swarm.smtp

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

		deliverySession ! 0
	}
}

object SMTPDeliveryService {
	def props: Props = Props(new SMTPDeliveryService)
}

case class DeliveryConfig(remoteHost: String, remotePort: Int, forceTLS: Boolean)

class SMTPDeliverySession extends Actor {
	override def receive: Receive = {
		case _ =>
	}
}

object SMTPDeliverySession {
	def props: Props = Props(new SMTPDeliverySession)
}
