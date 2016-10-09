package so.blacklight.swarm.smtp

import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.event.Logging
import akka.routing.{ActorRefRoutee, Router, SmallestMailboxPool, SmallestMailboxRoutingLogic}
import so.blacklight.swarm.control.StartService

/**
  * Created by xea on 10/1/2016.
  */
class SMTPService extends Actor {

  val logger = Logging(context.system, this)
  val smtpListener = context.actorOf(SMTPListener.props(SMTPConfig(1025, false)), "smtp-listener")
  val smtpSSLListener = context.actorOf(SMTPListener.props(SMTPConfig(1465, true)), "smtp-ssl-listener")

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

  val smtpRouter = context.actorOf(SmallestMailboxPool(5).props(Props[SMTPConnector]))

  override def receive: Receive = {
    case StartService => startService
    case Terminated(worker) => handleWorkerTermination(worker)
    case _ => ()
  }

  def startService = {
    smtpListener.!(AcceptConnections)(smtpRouter)
    smtpSSLListener.!(AcceptConnections)(smtpRouter)
  }

  def handleWorkerTermination(worker: ActorRef) = {
    /*
    smtpRouter = smtpRouter.removeRoutee(worker)

    val replacement = context.actorOf(Props[SMTPConnector])
    context watch replacement
    smtpRouter = smtpRouter.addRoutee(replacement)
    */
  }
}
