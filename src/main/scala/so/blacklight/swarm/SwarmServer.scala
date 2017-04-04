package so.blacklight.swarm

import akka.actor.{ActorSystem, Inbox, PoisonPill, Props}
/*
import so.blacklight.swarm.account.AccountService
import so.blacklight.swarm.control.StartService
import so.blacklight.swarm.echo.EchoService
import so.blacklight.swarm.http.HttpService
import so.blacklight.swarm.smtp.SMTPService
import so.blacklight.swarm.stats.StatService
import so.blacklight.swarm.storage.StorageService
*/

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

