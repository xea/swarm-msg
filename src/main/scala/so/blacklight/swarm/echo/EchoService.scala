package so.blacklight.swarm.echo

import akka.actor.Actor
import akka.event.Logging

/**
  */
class EchoService extends Actor {

  val logger = Logging(context.system, this)

  val echoListener = context.actorOf(EchoListener.props, "echo-listener")

  override def preStart() = {
    super.preStart()

  }

  override def receive: Receive = {
    case _ => ()
  }
}
