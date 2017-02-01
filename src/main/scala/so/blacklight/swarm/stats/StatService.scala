package so.blacklight.swarm.stats

import akka.actor.Actor
import so.blacklight.swarm.control.{StartService, StopService}

import scala.collection.mutable

/**
	*
	*/
class StatService extends Actor {

	private val stats = new mutable.HashMap[String, Long]()

	override def receive: Receive = {
		case StartService => startService()
		case StopService => stopService()
		case IncrementCounter(counter) => incrementCounter(counter)
		case DecrementCounter(counter) => decrementCounter(counter)
		case GetCounterValue(counter) =>
			if (counter.contains("*"))
				sender() ! getCounters(counter)
			else
				sender() ! getCounter(counter)
	}

	private def startService(): Unit = {
		stats.clear()
	}

	private def stopService(): Unit = {
		stats.clear()
	}

	private def incrementCounter(counter: String): Unit = {
		val currentValue = stats.getOrElseUpdate(counter, 0)
		stats.put(counter, currentValue + 1)
	}

	private def decrementCounter(counter: String): Unit = {
		val currentValue = stats.getOrElseUpdate(counter, 0)
		stats.put(counter, currentValue - 1)
	}

	private def getCounter(counter: String): StatEvent = {
		CounterValue(counter, stats.get(counter))
	}

	/**
		* Get all counters that match some given pattern (eg. stats.*.counter)
		*
		* @param counterPattern the counter pattern
		* @return a BatchCounterValue message containing all the counters matching the given pattern
		*/
	private def getCounters(counterPattern: String): StatEvent = {
		val preRegex = counterPattern.replaceAllLiterally(".", "\\.").replaceAllLiterally("*", ".*?")
		val regex = "^" + preRegex + "$"

		val values = stats.filter(_._1.matches(regex)).toMap

		BatchCounterValue(values)
	}

}
