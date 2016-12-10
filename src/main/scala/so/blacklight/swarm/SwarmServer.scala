package so.blacklight.swarm

import akka.actor.{ActorSystem, Inbox, PoisonPill, Props}
import so.blacklight.swarm.control.StartService
import so.blacklight.swarm.echo.EchoService
import so.blacklight.swarm.smtp.SMTPService

/**
  * Orchestrates the collection of defined services on a swarm node.
  */
class SwarmServer {

  private val ACTOR_SYSTEM_NAME = "swarm"

  private val system = ActorSystem(ACTOR_SYSTEM_NAME)

  private val inbox = Inbox.create(system)

  val smtpService = system.actorOf(Props[SMTPService], "smtpService")
  val echoService = system.actorOf(Props[EchoService], "echoService")

  def start(): Unit = {
    smtpService ! StartService
  }

  def stop(): Unit = {
    // let services finish their current tasks then shut down
    echoService ! PoisonPill
    smtpService ! PoisonPill
  }
}
