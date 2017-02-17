package so.blacklight.swarm.smtp

import java.net.Socket
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.util.{Failure, Random, Success}

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

		val nanos = System.nanoTime()
		val random = Random.nextLong()

		val sessionSuffix = s"-${random}-${nanos}"
		val clientSessionId = s"clientSession-${remoteAddress.replaceAll("/", "")}${sessionSuffix}"
		val protocolHandlerId = s"smtpProtocolHandler-${remoteAddress.replaceAll("/", "")}${sessionSuffix}"

		val clientSession = context.actorOf(SMTPClientSession.props(clientSocket), clientSessionId)
    val protocolHandler = context.actorOf(SMTPProtocolHandler.props(clientSession), protocolHandlerId)

    protocolHandler ! SMTPServerGreeting("Swarm SMTP")
  }


	def processQuit(): Unit = {
		println("Precious client has quit")
	}
}

