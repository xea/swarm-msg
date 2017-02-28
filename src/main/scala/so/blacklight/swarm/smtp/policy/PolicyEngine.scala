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

	private val policyExecutor = context.actorOf(SmallestMailboxPool(64).props(Props[PolicyExecutor]))

	override def receive: Receive = {
		case inputMessage: Email => determinePolicies(inputMessage)
			.foldLeft[Either[String, Email]](Right(inputMessage))((result, policy) =>
				result match {
					case Right(message) =>
						policy match {
							case asyncPolicy: AsyncAction =>
								policyExecutor ! ApplyAction(message, asyncPolicy)
								Right(message)
							case policy: EmailAction => policy.processEmail(message)
						}
					case error => error
				})
		case ActionError(error) => logger.warning(s"An error occurred during action evaluation: $error")
		// TODO implement a process validation mechanism
		case ActionApplied(_) => ()
	}

	def determinePolicies(email: Email): Seq[EmailAction] = {
		List(new SMTPDelivery(self))
	}
}

object PolicyEngine {

	def props(): Props = {
		Props(new PolicyEngine)
	}
}

class PolicyExecutor extends Actor {

	val logger = Logging(context.system, this)

	override def receive: Receive = {
		case ApplyAction(email, action) => sender() ! (action.processEmail(email) match {
			case Left(error) => ActionError(error)
			case Right(result) => ActionApplied(result)
		})
		case message => logger.warning(s"Unrecognised message: $message")
	}
}

object PolicyExecutor {
	def props(email: Email, action: EmailAction): Props = {
		Props(new PolicyExecutor)
	}
}

// A request to apply an action to an email
case class ApplyAction(email: Email, action: EmailAction)
// Describes the result of an action applied to the message
case class ActionApplied(email: Email)
// Describes an error happening during the execution of an action
case class ActionError(error: String)
