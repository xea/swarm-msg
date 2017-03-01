package so.blacklight.swarm.smtp

import akka.actor.{Actor, ActorRef}
import akka.event.Logging

/**
	*
	*/
class SMTPClientProtocol(clientSession: ActorRef, connector: ActorRef) extends Actor {

	import context._

	val logger = Logging(system, this)

	override def receive: Receive = {
		case _ => become(expectGreeting)
	}

	def expectGreeting: PartialFunction[Any, Unit] = {
		case SMTPServerGreeting =>
			sender() ! SMTPClientEhlo
	}
}
