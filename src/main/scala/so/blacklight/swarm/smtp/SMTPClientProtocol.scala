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

		case unknownMessage =>
			logger.warning(s"Unknown message received: $unknownMessage")
	}

	def expectEhlo: PartialFunction[Any, Unit] = {
		case SMTPServerEhlo(features) =>
			msgStream.headOption
				.map(email => {
					become(deliverMessage(email))
					self ! SMTPServerOk
				})
				.getOrElse(sender() ! SMTPClientQuit)

			/*
			features.filter(feature => "STARTTLS".equals(feature.toUpperCase))
				.headOption
			  .map(_ => sender() ! SMTPClientStartTLS)
			  .getOrElse(() =>
					become(deliverMessage(msgStream.head))
					sender() ! )
					*/
	}

	def deliverMessage(message: Email): PartialFunction[Any, Unit] = {
		case SMTPServerOk => ()
		case _ => ()
	}

}

object SMTPClientProtocol {

	def props(session: ActorRef, connector: ActorRef, msgStream: Stream[Email]): Props = {
		Props(new SMTPClientProtocol(session, connector, msgStream))
	}
}
