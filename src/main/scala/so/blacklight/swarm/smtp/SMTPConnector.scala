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

		val clientSessionId = s"clientSession-${clientSocket.getRemoteSocketAddress.toString.replaceAll("/", "")}"
		val protocolHandlerId = s"smtpProtoclHandler-${clientSocket.getRemoteSocketAddress.toString.replaceAll("/", "")}"

		val clientSession = context.actorOf(SMTPClientSession.props(clientSocket), clientSessionId)
    val protocolHandler = context.actorOf(SMTPProtocolHandler.props(clientSession), protocolHandlerId)

    protocolHandler ! SMTPServerGreeting("Swarm SMTP")
  }

}

