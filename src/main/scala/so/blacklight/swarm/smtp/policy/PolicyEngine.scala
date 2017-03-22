package so.blacklight.swarm.smtp.policy

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import so.blacklight.swarm.smtp.DeliveryConfig

/**
	*
	*/
class PolicyEngine extends Actor {

	import context._

	val logger = Logging(context.system, this)

	override def receive: Receive = {
		case ProcessEmail(email) =>
			become(initProcessing(sender()), false)

			self ! ProcessEmail(email)
		case _ =>
			logger.debug("MEH")
	}

	def initProcessing(sender: ActorRef): PartialFunction[Any, Unit] = {
		case ProcessEmail(email) =>
			become(processPolicies(getPolicies()), false)

			self ! ProcessEmail(email)

		case result: PolicyResult => {
			unbecome()

			logger.debug("Ide eljut azert?")

			sender ! result
		}
	}

	def processPolicies(policies: Stream[Props]): PartialFunction[Any, Unit] = {
		case ProcessEmail(email) =>
			logger.debug(s"Processing ${policies.length} remaining policies")
			policies match {
				case firstPolicy #:: remainingPolicies =>
					context.actorOf(firstPolicy) ! ProcessEmail(email)

					become(processPolicies(remainingPolicies), true)

				case _ =>
					// Processing has finished, do something
					logger.debug("Hit end of policy stream")
					unbecome()
			}

		case PolicyPass(email) =>
			become(processPolicies(policies.tail), true)

			self ! ProcessEmail(email)

		case PolicyReject(reason) =>
			unbecome()

			self ! PolicyReject(reason)

		case _ =>
			logger.debug("WTF")
			unbecome()
	}

	def getPolicies(): Stream[Props] = {
		Stream(
			//Props(new IgnoreLoopback),
			//SMTPDelivery.props(DeliveryConfig("localhost", 5025, false))
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
