package so.blacklight.swarm.smtp.policy

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import so.blacklight.swarm.mail.Email

/**
	*
	*/
class PolicyEngine extends Actor {

	val logger = Logging(context.system, this)

	override def receive: Receive = {
		case ProcessEmail(email) =>
			processEmail(email)
		case AsyncResult(result) =>
			updateAsyncResult(result)
	}

	def getPolicies(): Seq[Policy] = {
		List()
	}

	private def processEmail(email: Email): Unit = {
		// Step 1. iterate over the list of policies and do inline changes as necessary

		// Step 2. iterate over the generated effects and apply them
	}

	private def updateAsyncResult(result: PolicyResult): Unit = {

	}
}

/**
	* Encapsulates an otherwise synchronous policy
	* @param policy
	*/
class AsyncExecutor(policy: Policy, ref: ActorRef) extends Actor {

	override def receive: Receive = {
		case ProcessEmail(email) =>
			processAsync(email)
		case routedMessage =>
			ref ! routedMessage
	}

	def processAsync(email: Email): Unit = {
	}
}

/**
	* Represents the result of an asynchronous policy execution. Typically sent by an
	* AsyncExecutor to a PolicyEngine
	*
	* @param result the result of policy evaluation
	*/
final case class AsyncResult(result: PolicyResult)

/**
	* Offers helper functionality to ease creation of PolicyEngine instances
	*/
object PolicyEngine {

	def props(): Props = {
		Props(new PolicyEngine)
	}

}
