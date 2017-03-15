package so.blacklight.swarm

import akka.actor.{ActorSystem, Inbox, PoisonPill, Props}
import so.blacklight.swarm.account.AccountService
import so.blacklight.swarm.control.StartService
import so.blacklight.swarm.echo.EchoService
import so.blacklight.swarm.http.HttpService
import so.blacklight.swarm.smtp.SMTPService
import so.blacklight.swarm.stats.StatService
import so.blacklight.swarm.storage.StorageService

/**
	* Orchestrates the collection of defined services on a swarm node.
	*/
class SwarmServer {

	private val ACTOR_SYSTEM_NAME = "swarm"

	private val system = ActorSystem(ACTOR_SYSTEM_NAME)

	private val inbox = Inbox.create(system)

	// The following are the major service actors managed by the Swarm server
	private val smtpService = system.actorOf(Props[SMTPService], SwarmServer.SMTP_SERVICE)
	private val echoService = system.actorOf(Props[EchoService], SwarmServer.ECHO_SERVICE)
	private val httpService = system.actorOf(Props[HttpService], SwarmServer.HTTP_SERVICE)
	private val statService = system.actorOf(Props[StatService], SwarmServer.STAT_SERVICE)
	private val storageService = system.actorOf(Props[StorageService], SwarmServer.STORAGE_SERVICE)
	private val accountService = system.actorOf(Props[AccountService], SwarmServer.ACCOUNT_SERVICE)

	def start(): Unit = {
		smtpService ! StartService
		httpService ! StartService
		statService ! StartService
		storageService ! StartService
		accountService ! StartService
	}

	def stop(): Unit = {
		// let services finish their current tasks then shut down
		echoService ! PoisonPill
		smtpService ! PoisonPill
		httpService ! PoisonPill
		statService ! PoisonPill
		storageService ! PoisonPill
		accountService ! PoisonPill
	}
}

object SwarmServer {
	val SMTP_SERVICE: String = "smtpService"
	val ECHO_SERVICE: String = "echoService"
	val HTTP_SERVICE: String = "httpService"
	val STAT_SERVICE: String = "statService"
	val ACCOUNT_SERVICE: String = "accountService"
	val STORAGE_SERVICE: String = "storageService"
}
