package so.blacklight.swarm

import akka.actor.{ActorSystem, Inbox, PoisonPill, Props}
import so.blacklight.swarm.echo.EchoService
import so.blacklight.swarm.smtp.SMTPService

/**
  */
class SwarmServer {

  val ACTOR_SYSTEM_NAME = "swarm"

  val system = ActorSystem(ACTOR_SYSTEM_NAME)

  val inbox = Inbox.create(system)

  val smtpService = system.actorOf(Props[SMTPService], "smtpService")
  val echoService = system.actorOf(Props[EchoService], "echoService")

  def start = {
    //generator.tell(MessageReceived, ActorRef.noSender)
  }

  def stop = {
    // let actors finish their current tasks then shut down
    echoService ! PoisonPill
    smtpService ! PoisonPill
  }
}
