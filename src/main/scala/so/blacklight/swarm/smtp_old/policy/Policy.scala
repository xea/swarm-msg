package so.blacklight.swarm.smtp_old.policy

import so.blacklight.swarm.mail_old.Email

/**
	* Policies implement rules or transformations that are run against a message and may alter either
	* the processing workflow or the message itself.
	*/
sealed trait Policy {
	def process(email: Email): PolicyResult
}

