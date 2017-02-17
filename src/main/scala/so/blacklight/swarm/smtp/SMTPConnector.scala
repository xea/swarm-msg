package so.blacklight.swarm.smtp

import java.net.Socket

import akka.actor.Actor
import akka.event.Logging

import scala.util.Random

/**
  */
class SMTPConnector extends Actor {

  val logger = Logging(context.system, this)

  override def receive: Receive = {
    case ClientConnected(clientSocket) => processConnection(clientSocket)
		case ClientQuit => processQuit()
    case _ => println("Received message")
  }

  def processConnection(clientSocket: Socket): Unit = {
		val remoteAddress = clientSocket.getRemoteSocketAddress.toString

    logger.info(s"Processing connection from $remoteAddress")

		val nanos = System.nanoTime()
		val random = Random.nextLong()

		val sessionSuffix = "-%d-%d".format(random, nanos)
		val clientSessionId = "clientSession-%s%s".format(remoteAddress.replaceAll("/", ""), sessionSuffix)
		val protocolHandlerId = "smtpProtocolHandler-%s%s".format(remoteAddress.replaceAll("/", ""), sessionSuffix)

		val clientSession = context.actorOf(SMTPClientSession.props(clientSocket), clientSessionId)
    val protocolHandler = context.actorOf(SMTPProtocolHandler.props(clientSession), protocolHandlerId)

    protocolHandler ! SMTPServerGreeting("Swarm SMTP")
  }


	def processQuit(): Unit = {
		println("Precious client has quit")
	}
}

