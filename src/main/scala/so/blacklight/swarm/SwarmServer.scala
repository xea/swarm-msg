package so.blacklight.swarm

import akka.actor.{ActorRef, ActorSystem, Inbox, PoisonPill}
import so.blacklight.swarm.echo.EchoListener
import so.blacklight.swarm.smtp.{SMTPConfig, SMTPListener}

/**
  */
class SwarmServer {

  val ACTOR_SYSTEM_NAME = "swarm"

  val system = ActorSystem(ACTOR_SYSTEM_NAME)

  val inbox = Inbox.create(system)

  val smtpListener = system.actorOf(SMTPListener.props(SMTPConfig(25, false)), "smtp-listener")
  val smtpSSLListener = system.actorOf(SMTPListener.props(SMTPConfig(465, true)), "smtp-ssl-listener")
  val echoListener = system.actorOf(EchoListener.props, "echo-listener")
  //val generator = system.actorOf(Props[MessageGenerator], "messageGenerator")

  def start = {
    //generator.tell(MessageReceived, ActorRef.noSender)
  }

  def stop = {
    // let actors finish their current tasks then shut down
    smtpListener ! PoisonPill
  }
}
