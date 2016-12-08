package so.blacklight.swarm.smtp

import java.net.Socket

import akka.actor.{Actor, ActorRef}
import akka.event.Logging

/**
  *
  */
class SMTPConnector extends Actor {

  val logger = Logging(context.system, this)

	var protocolHandler: Option[ActorRef] = None

  override def receive: Receive = {
    case ClientConnected(clientSocket) => processConnection(clientSocket)
		case ClientQuit => println("Precious client has quit")
    case _ => println("Received message")
  }

  def processConnection(clientSocket: Socket): Unit = {
    logger.info(s"Processing connection from ${clientSocket.getRemoteSocketAddress.toString}")

		val clientSession = context.actorOf(SMTPClientSession.props(clientSocket), "clientSession")
    val protocolHandler = context.actorOf(SMTPProtocolHandler.props(clientSession), "protocolHandler")

    protocolHandler ! SMTPServerGreeting("Swarm SMTP")
  }

}

