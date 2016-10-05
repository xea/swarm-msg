package so.blacklight.swarm.smtp

import akka.actor.Actor

/**
  * Created by xea on 10/1/2016.
  */
class SMTPService extends Actor {

  val smtpListener = context.actorOf(SMTPListener.props(SMTPConfig(1025, false)), "smtp-listener")
  val smtpSSLListener = context.actorOf(SMTPListener.props(SMTPConfig(1465, true)), "smtp-ssl-listener")

  val smtpRouter 

  override def receive: Receive = {
    case _ => ()
  }
}
