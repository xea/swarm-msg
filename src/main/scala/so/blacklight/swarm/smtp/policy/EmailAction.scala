package so.blacklight.swarm.smtp.policy

import so.blacklight.swarm.mail.Email

/**
	*/
trait EmailAction {

	def processEmail(email: Email): Unit
}

class ModifySender extends EmailAction {
	override def processEmail(email: Email): Unit = {
	}
}
