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
		case _ => ()
	}
}

object SMTPDeliveryService {
	def props: Props = Props(new SMTPDeliveryService)
}