package so.blacklight.swarm.smtp

import akka.actor.Actor

/**
  * Created by xea on 10/1/2016.
  */
class SMTPService extends Actor {

  val smtpListener = context.actorOf(SMTPListener.props(SMTPConfig(25, false)), "smtp-listener")
  val smtpSSLListener = context.actorOf(SMTPListener.props(SMTPConfig(465, true)), "smtp-ssl-listener")

  override def receive: Receive = {
    case _ => ()
  }
}
