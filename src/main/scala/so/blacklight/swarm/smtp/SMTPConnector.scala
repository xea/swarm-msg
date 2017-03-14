package so.blacklight.swarm.smtp

import java.net.Socket

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import so.blacklight.swarm.mail.Email
import so.blacklight.swarm.smtp.policy.PolicyEngine

import scala.util.Random

/**
  */
class SMTPConnector extends Actor {

  val logger = Logging(context.system, this)

  override def receive: Receive = {
    case ClientConnected(clientSocket) => processConnection(clientSocket)
		case ClientQuit => processQuit()
		case ReceivedMessage(email) => processPolicies(email)
		case DeliverMessage(socket, message) => processDelivery(socket, Stream(message))
		case DeliverMessages(socket, messages) => processDelivery(socket, messages)
    case _ => println("Received message")
  }

  def processConnection(clientSocket: Socket): Unit = {
    logger.info(s"Processing connection from ${clientSocket.getRemoteSocketAddress.toString}")

		val session = initSession(clientSocket)
		val protocolHandler = context.actorOf(SMTPServerProtocol.props(session, self))

    protocolHandler ! SMTPServerGreeting("Swarm SMTP")
  }

	def processPolicies(email: Email): Unit = {
		logger.info("Transaction finished, processing message")

		val policyEngine = context.actorOf(PolicyEngine.props(), "policyEngine-%s".format(Random.nextLong()))

		policyEngine ! email
	}

	def processQuit(): Unit = {
		println("Precious client has quit")
	}

	def processDelivery(socket: Socket, messages: Stream[Email]): Unit = {
		val session = initSession(socket)
		val protocolHandler = context.actorOf(SMTPClientProtocol.props(session, self, messages))

		protocolHandler ! InitTransaction
	}

	private def initSession(clientSocket: Socket): ActorRef = {
		val remoteAddress = clientSocket.getRemoteSocketAddress.toString

		val sessionId = SessionID()

		val suffix = "%s-%s".format(remoteAddress.replaceAll("/", ""), sessionId.toString)

		val clientSessionId = "clientSession-%s".format(suffix)
		val protocolHandlerId = "smtpProtocolHandler-%s".format(suffix)

		context.actorOf(SMTPClientSession.props(clientSocket, sessionId), clientSessionId)
	}
}

object SMTPConnector {
	def props(): Props = {
		Props(new SMTPConnector)
	}
}
