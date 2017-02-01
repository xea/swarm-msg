package so.blacklight.swarm.http

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import so.blacklight.swarm.control.{StartService, StopService}
import so.blacklight.swarm.stats.{BatchCounterValue, CounterValue, GetCounterValue}
import spark.Spark.{get, port, stop}
import spark.template.jade.JadeTemplateEngine
import spark.{ModelAndView, Request, Response}

import scala.concurrent.Await

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
		logger.info(s"HTTP service started, listening on port ${port}")
	}

	private def stopService(): Unit = {
		stop()
	}

	private def mountEndpoints(): Unit = {
		get("/", showHomePage)
		get("/stats", showStatistics)
		get("/hello", (rq, rs) => { new ModelAndView(null, "hello") }, new JadeTemplateEngine())
	}

	private def showHomePage(request: Request, response: Response): String = {
		"<h1>Swarm SMTP</h1>OK"
	}

	private def showStatistics(request: Request, response: Response): String = {
		implicit val timeout = Timeout(5, TimeUnit.SECONDS)
		val message = GetCounterValue("*")

		val future = context.actorSelection("/user/statService") ? message

		Await.result(future, timeout.duration) match {
			case CounterValue(ctr, value) => value.map(stat => s"$ctr: $stat").getOrElse("No statistics found")
			case BatchCounterValue(values) => s"<h1>Swarm SMTP</h1>" + values.map(keyValue => s"${keyValue._1}: ${keyValue._2}").mkString("\n")
			case unknownMessage => s"Unknown message received: $unknownMessage"
		}
	}

	private def showTemplate(request: Request, response: Response): ModelAndView = {
		new ModelAndView(null, "asdf")
	}

}
