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
			logger.info(s"Received client connection from: $hostId")
			sender() ! SMTPServerEhlo(Array("THIS", "THAT"))
			become(expectEmail)
		}
	}

	def expectEmail: PartialFunction[Any, Unit] = {
		case SMTPClientMailFrom(mailFrom) => {
			println(s"Look, $mailFrom wants to send a message")
			sender() ! SMTPServerOk
		}
		case SMTPClientReceiptTo(recipient) => {
			println(s"Furthermore, the recipient is $recipient")
			sender() ! SMTPServerOk
		}
		case SMTPClientDataBegin => {
			sender() ! SMTPServerDataOk
		}
		case SMTPClientReset => {
			unbecome()
		}
		case SMTPClientQuit => {
			sender() ! SMTPServerQuit
		}
	}
}

object SMTPProtocolHandler {

  def props(clientSession: ActorRef): Props = Props(new SMTPProtocolHandler(clientSession))

}
