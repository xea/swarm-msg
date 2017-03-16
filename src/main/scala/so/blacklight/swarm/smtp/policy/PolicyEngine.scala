package so.blacklight.swarm.smtp.policy

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import so.blacklight.swarm.mail.Email

class PolicyEngine extends Actor {

	override def receive: Receive = {
		case ProcessEmail(email) =>
			getPolicies(email).foldLeft[ActionResult](ActionPass(email))((state, currentPolicy) => {
				state match {
					case ActionPass(email) =>
						currentPolicy.processEmail(email)
					case terminalAction =>
						terminalAction
				}
			})
	}

	private def getPolicies(email: Email): Seq[EmailPolicy] = {
		List()
	}

	private def applyPolicy(email: Email, policy: EmailAction): ActionResult = {
		ActionPass(email)
	}

}

trait EmailPolicy {
	def processEmail(email: Email): ActionResult
}


sealed trait ActionResult
final case class ActionPass(emailResult: Email) extends ActionResult
final case class ActionReject(reason: String) extends ActionResult

sealed trait PolicyEvent

final case class ProcessEmail(email: Email)

/**
	*
	*/
class PolicyEngine2 extends Actor {

	val logger = Logging(context.system, this)

	override def receive: Receive = {
		case inputMessage: Email =>
			processPolicies(inputMessage, determinePolicies(inputMessage))

		case ActionError(error) =>
			logger.warning(s"An error occurred during action evaluation: $error")
		// TODO implement a process validation mechanism

		case ActionApplied(_) => logger.info("Policy applied successfully")
	}

	private def processPolicies(message: Email, policies: Seq[Either[ActorRef, EmailAction]]): Unit = {
		policies.foldLeft[Either[String, Email]](Right(message))((result, policy) =>
			result match {

				case Right(message) =>
					policy match {
						// Asynchronous policies are expected to be actors
						case Left(actorRef) =>
							actorRef ! ApplyAction(message)
							Right(message)

						// Regular (synchronous) policies are evaluated in-line
						case Right(syncPolicy) =>
							syncPolicy.processEmail(message)
					}

				case error => error
			})
	}

	def determinePolicies(email: Email): Seq[Either[ActorRef, EmailAction]] = {
		List(
			Left(
				context.actorOf(Props(new SMTPDelivery()))))
	}
}

object PolicyEngine {

	def props(): Props = {
		Props(new PolicyEngine)
	}
}

// A request to apply an action to an email
case class ApplyAction(email: Email)
// Describes the result of an action applied to the message
case class ActionApplied(email: Email)
// Describes an error happening during the execution of an action
case class ActionError(error: String)

