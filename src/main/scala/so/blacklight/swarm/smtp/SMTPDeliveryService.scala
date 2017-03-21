package so.blacklight.swarm.smtp

import akka.actor.{Actor, Props}
import akka.event.Logging

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
		case _ => ()
	}
}

object SMTPDeliveryService {
	def props: Props = Props(new SMTPDeliveryService)
}

case class DeliveryConfig(remoteHost: String, remotePort: Int, forceTLS: Boolean)
