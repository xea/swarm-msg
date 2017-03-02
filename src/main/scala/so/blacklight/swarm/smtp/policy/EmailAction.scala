package so.blacklight.swarm.smtp.policy

import java.net.Socket

import akka.actor.Actor
import so.blacklight.swarm.mail.Email
import so.blacklight.swarm.smtp.{DeliverMessage, SMTPConnector}

// TODO implement effectful actions, similarly to Idris' system


/**
	*/
trait EmailAction {

	def processEmail(email: Email): Either[String, Email]
}

trait AsyncAction extends Actor with EmailAction

class ModifySender extends EmailAction {
	override def processEmail(email: Email): Either[String, Email] = {
		Right(email)
	}
}

class SMTPDelivery extends AsyncAction {

	def processEmail(email: Email): Either[String, Email] = {
		try {
			val socket = new Socket("localhost", 5025)

			val connector = context.actorOf(SMTPConnector.props())

			connector ! DeliverMessage(socket, email)

			Right(email)
		} catch {
			case ex: Exception => Left(ex.getMessage)
		}
	}

	override def receive: Receive = {
		case _ => ()
	}
}
