package so.blacklight.swarm.http

import akka.actor.Actor
import so.blacklight.swarm.control.StartService

/**
	*
	*/
class HttpService extends Actor {

	override def receive: Receive = {
		case StartService => startService()
	}

	private def startService() = {

	}
}
