package so.blacklight.swarm.smtp

import java.net.Socket

import akka.actor.{Actor, ActorRef}
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
    case _ => println("Received message")
  }

  def processConnection(clientSocket: Socket): Unit = {
    logger.info(s"Processing connection from ${clientSocket.getRemoteSocketAddress.toString}")

		val protocolHandler = initSession(clientSocket)

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

	private def initSession(clientSocket: Socket): ActorRef = {
		val remoteAddress = clientSocket.getRemoteSocketAddress.toString

		val sessionId = SessionID()

		val nanos = System.nanoTime()
		val random = Random.nextLong()

		val suffix = "%s-%s".format(remoteAddress.replaceAll("/", ""), sessionId.toString)

		val clientSessionId = "clientSession-%s".format(suffix)
		val protocolHandlerId = "smtpProtocolHandler-%s".format(suffix)

		val clientSession = context.actorOf(SMTPClientSession.props(clientSocket, sessionId), clientSessionId)
		val protocolHandler = context.actorOf(SMTPServerProtocol.props(clientSession, self), protocolHandlerId)

		protocolHandler
	}
}

