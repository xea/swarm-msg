package so.blacklight.swarm.smtp

import java.net.Socket

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import so.blacklight.swarm.mail.Email
import so.blacklight.swarm.smtp.policy.{PolicyEngine, SMTPDelivery}

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

		val nanos = System.nanoTime()
		val random = Random.nextLong()

		val sessionSuffix = "-%d-%d".format(random, nanos)
		val clientSessionId = "clientSession-%s%s".format(remoteAddress.replaceAll("/", ""), sessionSuffix)
		val protocolHandlerId = "smtpProtocolHandler-%s%s".format(remoteAddress.replaceAll("/", ""), sessionSuffix)

		val clientSession = context.actorOf(SMTPClientSession.props(clientSocket), clientSessionId)
		val protocolHandler = context.actorOf(SMTPProtocolHandler.props(clientSession, self), protocolHandlerId)

		protocolHandler
	}
}

