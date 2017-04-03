package so.blacklight.swarm.smtp_old.policy

import akka.actor.Actor

class IgnoreLoopback extends Actor {
	override def receive: Receive = {
		case ProcessEmail(email) =>
			email.isLoopback() match {
				case true =>
					sender() ! PolicyReject("Loopback message")
				case false =>
					sender() ! PolicyPass(email)
			}
	}
}
