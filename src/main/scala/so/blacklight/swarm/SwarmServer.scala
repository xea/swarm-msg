package so.blacklight.swarm

import javafx.geometry.Pos

import akka.actor.{ActorSystem, Inbox, PoisonPill, Props}
import so.blacklight.swarm.control.StartService
import so.blacklight.swarm.echo.EchoService
import so.blacklight.swarm.http.HttpService
import so.blacklight.swarm.smtp.SMTPService
import so.blacklight.swarm.stats.StatService
import so.blacklight.swarm.storage.ObjectStorage

/**
	* Orchestrates the collection of defined services on a swarm node.
	*/
class SwarmServer {

	private val ACTOR_SYSTEM_NAME = "swarm"

	private val system = ActorSystem(ACTOR_SYSTEM_NAME)

	private val inbox = Inbox.create(system)

	// The following are the major service actors managed by the Swarm server
	private val smtpService = system.actorOf(Props[SMTPService], "smtpService")
	private val echoService = system.actorOf(Props[EchoService], "echoService")
	private val httpService = system.actorOf(Props[HttpService], "httpService")
	private val statService = system.actorOf(Props[StatService], "statService")
	private val storageService = system.actorOf(Props[ObjectStorage], "objectStorageService")

	def start(): Unit = {
		smtpService ! StartService
		httpService ! StartService
		statService ! StartService
		storageService ! StartService
	}

	def stop(): Unit = {
		// let services finish their current tasks then shut down
		echoService ! PoisonPill
		smtpService ! PoisonPill
		httpService ! PoisonPill
		statService ! PoisonPill
		storageService ! PoisonPill
	}
}
