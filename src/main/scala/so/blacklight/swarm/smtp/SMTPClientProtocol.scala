package so.blacklight.swarm.smtp

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import so.blacklight.swarm.mail.Email

/**
	*
	*/
class SMTPClientProtocol(clientSession: ActorRef, connector: ActorRef, msgStream: Stream[Email]) extends Actor {

	import context._

	val logger = Logging(system, this)

	override def receive: Receive = {
		case InitTransaction =>
			clientSession ! InitTransaction
			become(expectGreeting)

		case _ => become(expectGreeting)
	}

	def expectGreeting: PartialFunction[Any, Unit] = {
		case SMTPServerGreeting =>
			sender() ! SMTPClientEhlo
			become(expectEhlo)
		case SMTPServerServiceNotAvailable =>
			sender() ! SMTPClientQuit
	}

	def expectEhlo: PartialFunction[Any, Unit] = {
		case SMTPServerEhlo(features) =>
			sender() ! SMTPClientMailFrom("senda")
	}

	def processEhlo(): SMTPClientEvent = {

	}
}

object SMTPClientProtocol {

	def props(session: ActorRef, connector: ActorRef): Props = {
		Props(new SMTPClientProtocol(session, connector))
	}
}
