package so.blacklight.swarm.smtp

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging

class SMTPProtocolHandler(clientSession: ActorRef) extends Actor {

	import context._

	val logger = Logging(context.system, this)

	override def receive: Receive = {
		case greeting @ SMTPServerGreeting(_) =>
			clientSession ! greeting
			become(expectEhlo)
	}

	def expectEhlo: PartialFunction[Any, Unit] = {
		case SMTPClientEhlo(hostId) => {
			println(s"Got client $hostId")
			sender() ! SMTPServerEhlo(Array("THIS", "THAT"))
			become(expectEmail)
			//unbecome()
		}
	}

	def expectEmail: PartialFunction[Any, Unit] = {
		case SMTPClientMailFrom(sender) => {
			println(s"Look $sender wants to send a message")
		}
		case SMTPClientDataBegin => {
			sender() ! SMTPServerDataOk
		}
		case SMTPClientReset => {

		}
		case SMTPClientQuit => {
			sender() ! SMTPServerQuit
		}
	}
}

object SMTPProtocolHandler {

  def props(clientSession: ActorRef): Props = Props(new SMTPProtocolHandler(clientSession))

}
