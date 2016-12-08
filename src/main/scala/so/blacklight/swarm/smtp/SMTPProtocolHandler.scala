package so.blacklight.swarm.smtp

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging

class SMTPProtocolHandler(clientSession: ActorRef) extends Actor {

	import context._

	val logger = Logging(context.system, this)

	/*
  override def receive: Receive = {
    case greeting @ SMTPServerGreeting(_) =>
			clientSession.send(greeting)
			clientSession.readReply() match {
				case mailFrom @ SMTPClientMailFrom(_) => {
					clientSession.send(SMTPServerOk)
				}
				case SMTPClientQuit => sender() ! ClientQuit
				case _ => println("Got answer")
			}
		case msg => logger.warning(s"Unrecognised protocol message: $msg")
  }
  */

	override def receive: Receive = {
		case greeting @ SMTPServerGreeting(_) =>
			clientSession ! greeting
			become(expectMail)
	}

	def expectMail: PartialFunction[Any, Unit] = {
		case SMTPClientEhlo(hostId) => {
			println(s"Got client $hostId")
			unbecome()
		}
	}
}

object SMTPProtocolHandler {

  def props(clientSession: ActorRef): Props = Props(new SMTPProtocolHandler(clientSession))

}
