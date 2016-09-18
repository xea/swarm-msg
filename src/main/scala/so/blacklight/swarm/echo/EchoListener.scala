package so.blacklight.swarm.echo

import akka.actor.{Actor, Props}
import akka.event.Logging

/**
  * A simple network echo service that listens on a plain TCP port, expects any plain text message terminated
  * by an LF/CRLF sequence and sends the same message back to the sender
  */
class EchoListener extends Actor {

  val logger = Logging(context.system, this)

  override def preStart = {
    super.preStart
    logger.info("Echo service starting up")
  }

  override def receive: Receive = {
    case _ => ()
  }
}

object EchoListener {

  def props: Props = Props(new EchoListener)

}
