package so.blacklight.swarm.http

import akka.actor.Actor
import akka.event.Logging
import spark.Spark.{before, get, post, stop}
import so.blacklight.swarm.control.{StartService, StopService}
import spark.{Filter, Request, Response}

/**
	*
	*/
class HttpService extends Actor {

	val logger = Logging(context.system, this)

	override def receive: Receive = {
		case StartService => startService()
		case StopService => stopService()
	}

	private def startService(): Unit = {
		logger.info("HTTP service starting up")
		mountEndpoints()
		logger.info("HTTP service started")
	}

	private def stopService(): Unit = {
		stop()
	}

	private def mountEndpoints(): Unit = {
		val f: Filter = requireAuthentication;
		before(f)
		get("/", showHomePage)
		get("/get/:emailid", showStatistics)
	}

	private def requireAuthentication(request: Request, response: Response): Unit = {

	}

	private def showHomePage(request: Request, response: Response): String = {
		"<h1>Swarm SMTP</h1>OK"
	}

	private def showStatistics(request: Request, response: Response): String = {
		val emailId = request.params("emailid")

		emailId
	}
}
