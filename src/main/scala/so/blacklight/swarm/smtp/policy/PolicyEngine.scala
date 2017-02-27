package so.blacklight.swarm.smtp.policy

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.routing.SmallestMailboxPool
import so.blacklight.swarm.mail.Email

/**
	*
	*/
class PolicyEngine extends Actor {

	val logger = Logging(context.system, this)

	private val policyExecutor = context.actorOf(SmallestMailboxPool(8).props(Props[PolicyExecutor]))

	override def receive: Receive = {
		case email: Email => determinePolicies(email)
			.foldLeft(email)((e, policy) =>
				policy match {
					case asyncPolicy: AsyncAction =>
						policyExecutor ! asyncPolicy
						email
					case policy: EmailAction => policy.processEmail(email)
				})
	}

	def determinePolicies(email: Email): Seq[EmailAction] = {
		List(new SMTPDelivery)
	}
}

object PolicyEngine {

	def props(): Props = {
		Props(new PolicyEngine)
	}
}

class PolicyExecutor extends Actor {
	override def receive: Receive = {

	}
}
