package so.blacklight.swarm.smtp

import java.net.Socket

import akka.actor.{Actor, ActorRef}
import akka.event.Logging

/**
  */
class SMTPConnector extends Actor {

  val logger = Logging(context.system, this)

	var protocolHandler: Option[ActorRef] = None

  override def receive: Receive = {
    case ClientConnected(clientSocket) => processConnection(clientSocket)
		case ClientQuit => processQuit()
    case _ => println("Received message")
  }

  def processConnection(clientSocket: Socket): Unit = {
		val remoteAddress = clientSocket.getRemoteSocketAddress.toString

    logger.info(s"Processing connection from $remoteAddress")

		val clientSessionId = s"clientSession-${remoteAddress.replaceAll("/", "")}"
		val protocolHandlerId = s"smtpProtocolHandler-${remoteAddress.replaceAll("/", "")}"

		val clientSession = context.actorOf(SMTPClientSession.props(clientSocket), clientSessionId)
    val protocolHandler = context.actorOf(SMTPProtocolHandler.props(clientSession), protocolHandlerId)

    protocolHandler ! SMTPServerGreeting("Swarm SMTP")
  }

	def processQuit(): Unit = {
		println("Precious client has quit")
	}
}

