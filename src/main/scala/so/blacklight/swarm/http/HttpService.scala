package so.blacklight.swarm.http

import java.util
import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import so.blacklight.swarm.control.{StartService, StopService}
import so.blacklight.swarm.stats.{BatchCounterValue, CounterValue, GetCounterValue}
import spark.Spark.{get, stop}
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
		logger.info("HTTP service started")
	}

	private def stopService(): Unit = {
		stop()
	}

	private def mountEndpoints(): Unit = {
		val jade = new JadeTemplateEngine()

		// TODO pretty printing is here temporarily. Remove when it's not needed any more
		jade.configuration().setPrettyPrint(true)

		get("/", (req, resp) => showIndex(req, resp), jade)
		get("/stats", (req, resp) => showStatistics(req, resp), jade)
	}

	private def showIndex(request: Request, response: Response): ModelAndView = {
		Template("index")
			.title("Swarm Msg")
		  .render()
	}

	private def showStatistics(request: Request, response: Response): ModelAndView = {
		implicit val timeout = Timeout(5, TimeUnit.SECONDS)
		val message = GetCounterValue("*")

		val future = context.actorSelection("/user/statService") ? message

		Await.result(future, timeout.duration) match {
			case CounterValue(ctr, value) =>
				Template("stats")
					.set("stats", value.map(stat => s"$ctr: $stat").getOrElse("No statistics found"))
					.render()
			case BatchCounterValue(values) =>
				Template("stats")
				  .set("stats", values.map(entry => s"${entry._1}: ${entry._2}").mkString("\n"))
					.render()
			case _ => Template("stats-error").render()
		}
	}
}

class Template(templateName: String) {

	var pageTitle: Option[String] = None

	val templateModel = new util.HashMap[String, String]()

	def title(): String = pageTitle.getOrElse("No title")

	def title(newTitle: String): Template = {
		pageTitle = Some(newTitle)
		this
	}

	def set(key: String, value: String): Template = {
		templateModel.put(key, value)
		this
	}

	def set(key: String, values: Map[String, String]): Template = {
		values
			.filter(entry => entry._1 != null && entry._2 != null)
			.foreach(entry => templateModel.put(entry._1, entry._2))
		this
	}

	def set(elems: (String, String)*): Template = {
		// This is a poor solution probably, but Map(elems) doesn't work here...
		elems.foreach(entry => set(entry._1, entry._2))
		this
	}

	/**
		* Convert this Template into a ModelAndView instance
		* @return ModelAndView instance
		*/
	def render(): ModelAndView = {
		new ModelAndView(templateModel, templateName)
	}
}

/**
	* Companion object helping creating Template instances easier
	*/
object Template {
	def apply(templateName: String): Template = {
		new Template(templateName)
	}
}

