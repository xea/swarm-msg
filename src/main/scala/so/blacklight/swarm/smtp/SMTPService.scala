package so.blacklight.swarm.smtp

import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.event.Logging
import akka.routing.SmallestMailboxPool
import so.blacklight.swarm.control.StartService

/**
  * SMTPService maintains and
  */
class SMTPService extends Actor {

  private val NUMBER_OF_CONNECTORS: Int = 16

  val logger = Logging(context.system, this)

  // TODO assign port numbers from configuration
  val smtpListener: ActorRef = context.actorOf(SMTPListener.props(SMTPConfig(1025, false)), "smtp-listener")
  val smtpSSLListener: ActorRef = context.actorOf(SMTPListener.props(SMTPConfig(1465, true)), "smtp-ssl-listener")
  val smtpDeliveryService: ActorRef = context.actorOf(SMTPDeliveryService.props, "smtp-delivery")

  /**
    * Reminder of routing logics:
    *
    * akka.routing.RoundRobinRoutingLogic
    * akka.routing.RandomRoutingLogic
    * akka.routing.SmallestMailboxRoutingLogic
    * akka.routing.BroadcastRoutingLogic
    * akka.routing.ScatterGatherFirstCompletedRoutingLogic
    * akka.routing.TailChoppingRoutingLogic
    * akka.routing.ConsistentHashingRoutingLogic
    */
  /*
  var smtpRouter = Router(SmallestMailboxRoutingLogic(), Vector.fill(5) {
    val worker = context.actorOf(Props[SMTPConnector])
    context watch worker
    ActorRefRoutee(worker)
  })
  */

  private val smtpRouter = context.actorOf(SmallestMailboxPool(NUMBER_OF_CONNECTORS).props(Props[SMTPConnector]))

  override def receive: Receive = {
    case StartService => startService()
    case Terminated(worker) => handleWorkerTermination(worker)
    case _ => ()
  }

  def startService() = {
    (smtpListener ! StartService)(smtpRouter)
    (smtpSSLListener ! StartService)(smtpRouter)
    smtpDeliveryService ! StartService
  }

  def handleWorkerTermination(worker: ActorRef): Unit = {
    /*
    smtpRouter = smtpRouter.removeRoutee(worker)

    val replacement = context.actorOf(Props[SMTPConnector])
    context watch replacement
    smtpRouter = smtpRouter.addRoutee(replacement)
    */
  }
}
