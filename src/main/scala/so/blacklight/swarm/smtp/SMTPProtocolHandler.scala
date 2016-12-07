package so.blacklight.swarm.smtp

import akka.actor.{Actor, Props}
import akka.event.Logging

class SMTPProtocolHandler(clientSession: SMTPClientSession) extends Actor {

	val logger = Logging(context.system, this)

  override def receive: Receive = {
    case greeting @ SMTPServerGreeting(_) =>
			clientSession.send(greeting)
			clientSession.readReply() match {
				case SMTPClientQuit => sender() ! ClientQuit
				case _ => println("Got answer")
			}
		case msg => logger.warning(s"Unrecognised protocol message: $msg")
  }
}

object SMTPProtocolHandler {

  def props(clientSession: SMTPClientSession): Props = Props(new SMTPProtocolHandler(clientSession))

}
