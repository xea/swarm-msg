package so.blacklight.swarm.smtp.policy

import java.net.Socket

import akka.actor.ActorRef
import so.blacklight.swarm.mail.Email

// TODO implement effectful actions, similarly to Idris' system

/**
	*/
trait EmailAction {

	def processEmail(email: Email): Either[String, Email]
}

abstract class AsyncAction(supervisor: ActorRef) extends EmailAction {

}

class ModifySender extends EmailAction {
	override def processEmail(email: Email): Either[String, Email] = {
		Right(email)
	}
}

class SMTPDelivery(supervisor: ActorRef) extends AsyncAction(supervisor) {

	override def processEmail(email: Email): Either[String, Email] = {
		try {
			val socket = new Socket("localhost", 5025)
			Right(email)
		} catch {
			case ex: Exception => Left(ex.getMessage)
		}
	}
}
