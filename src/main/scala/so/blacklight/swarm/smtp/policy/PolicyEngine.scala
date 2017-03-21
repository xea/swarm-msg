package so.blacklight.swarm.smtp.policy

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import so.blacklight.swarm.mail.Email

/**
	*
	*/
class PolicyEngine extends Actor {

	import context._

	val logger = Logging(context.system, this)

	override def receive: Receive = {
		case ProcessEmail(email) =>
			become(processPolicies(getPolicies()), false)

			self ! ProcessEmail(email)

	}

	def processPolicies(policies: Stream[Props]): PartialFunction[Any, Unit] = {
		case ProcessEmail(email) =>
			policies match {
				case firstPolicy #:: remainingPolicies =>
					context.actorOf(firstPolicy) ! ProcessEmail(email)

					become(processPolicies(remainingPolicies), false)

				case _ =>
					// Processing has finished, do something
					unbecome()
			}

		case PolicyPass(email) =>
			become(processPolicies(policies.tail), true)

			self ! ProcessEmail(email)

		case _ =>
			unbecome()
	}

	def getPolicies(): Stream[Props] = {
		Stream(
			SMTPDelivery.props(DeliveryConfig("localhost", 5025, false))
		)
	}
}

/**
	* Offers helper functionality to ease creation of PolicyEngine instances
	*/
object PolicyEngine {

	def props(): Props = {
		Props(new PolicyEngine)
	}

}
