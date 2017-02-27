package so.blacklight.swarm.smtp.policy

import so.blacklight.swarm.mail.Email

/**
	*/
trait EmailAction {

	def processEmail(email: Email): Email
}

trait AsyncAction

class ModifySender extends EmailAction {
	override def processEmail(email: Email): Email = {
		email
	}
}

class SMTPDelivery extends EmailAction {

	override def processEmail(email: Email): Email = {
		email
	}
}
