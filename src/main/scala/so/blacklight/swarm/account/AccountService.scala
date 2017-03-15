package so.blacklight.swarm.account

import akka.actor.Actor
import akka.event.Logging
import so.blacklight.swarm.control.StartService

/**
	*
	*/
class AccountService extends Actor {

	import context._

	val logger = Logging(context.system, this)

	override def receive: Receive = {
		case StartService =>
			startService()
			become(expectQuery, false)
	}

	def expectQuery: PartialFunction[Any, Unit] = {
		case LookupAccount(query) => sender() ! findAccount(query)
			.map(AccountInfo)
			.getOrElse(NoSuchAccount)

		case otherMessage =>
			logger.warning(s"Expected account query, got: $otherMessage")
	}

	private def findAccount(query: (Account) => Boolean): Option[Account] = {
		// TODO implement this
		None
	}

	private def startService(): Unit = {
		logger.info("Account service has been started")
	}
}
