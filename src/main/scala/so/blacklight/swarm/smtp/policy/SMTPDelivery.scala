package so.blacklight.swarm.smtp.policy

import akka.actor.{Actor, ActorRef, Props}
import so.blacklight.swarm.mail_old.Email
import so.blacklight.swarm.smtp.{DeliverMessage, DeliveryConfig}

/**
	*
	*/
class SMTPDelivery(config: DeliveryConfig) extends Actor with DeliveryEffect {

	import context._

	def doDelivery(email: Email): Unit = {
		context.actorSelection("/user/smtpService/smtp-delivery") ! DeliverMessage(email)
	}

	override def receive: Receive = {
		case ProcessEmail(email) =>
			doDelivery(email)

			become(awaitDelivery(email, sender()))
		case _ => ()
	}

	def awaitDelivery(email: Email, sender: ActorRef): PartialFunction[Any, Unit] = {
		case _ =>
			sender ! PolicyPass(email)
			unbecome()
	}
}

object SMTPDelivery {
	def props(config: DeliveryConfig): Props = Props(new SMTPDelivery(config))
}

