package so.blacklight.swarm.smtp.policy

import so.blacklight.swarm.mail.Email

/**
	*
	*/
class SMTPDelivery(config: DeliveryConfig) extends DeliveryEffect {

	def doDelivery(email: Email): Unit = {

	}
}

case class DeliveryConfig(remoteHost: String, remotePort: Int, forceTLS: Boolean)
