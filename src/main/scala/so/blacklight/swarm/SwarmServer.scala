package so.blacklight.swarm

import akka.actor.{ActorSystem, Inbox}
/**
	* Orchestrates the collection of defined services on a swarm node.
	*/
class SwarmServer {

	private val ACTOR_SYSTEM_NAME = "swarm"

	private val system = ActorSystem(ACTOR_SYSTEM_NAME)

	private val inbox = Inbox.create(system)

	//private val smtpService = system.actorOf(Props[SMTPService], SwarmServer.SMTP_SERVICE)

	def start(): Unit = {
	}
}

