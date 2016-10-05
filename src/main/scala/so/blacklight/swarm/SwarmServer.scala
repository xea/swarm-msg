package so.blacklight.swarm

import akka.actor.{ActorSystem, Inbox, PoisonPill, Props}
import so.blacklight.swarm.echo.EchoService
import so.blacklight.swarm.smtp.SMTPService
import so.blacklight.swarm.stream.{StreamService, TriggerStream}

/**
  *
  */
class SwarmServer {

  private val ACTOR_SYSTEM_NAME = "swarm"

  val system = ActorSystem(ACTOR_SYSTEM_NAME)

  val inbox = Inbox.create(system)

  val smtpService = system.actorOf(Props[SMTPService], "smtpService")
  val echoService = system.actorOf(Props[EchoService], "echoService")
	val streamService = system.actorOf(Props[StreamService], "streamService")

  def start = {
    //generator.tell(MessageReceived, ActorRef.noSender)
		streamService ! TriggerStream
  }

  def stop = {
    // let actors finish their current tasks then shut down
    echoService ! PoisonPill
    smtpService ! PoisonPill
  }
}
