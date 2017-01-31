package so.blacklight.swarm.smtp

import java.net.{InetSocketAddress, ServerSocket, Socket}
import java.security.SecureRandom
import javax.net.ServerSocketFactory
import javax.net.ssl.{SSLContext, SSLServerSocket}

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import so.blacklight.swarm.net.tls.PermissiveTrustManager
import so.blacklight.swarm.stats.IncrementCounter

/**
  */
class SMTPListener(config: SMTPConfig) extends Actor {

	private val TLS_VERSION = "TLSv1.2"

  val logger = Logging(context.system, this)

	var listenSocket: Option[ServerSocket] = None

	var dispatcher: Option[ActorRef] = None

  override def preStart: Unit = {
    super.preStart

    if (config.ssl) {
      logger.info("SMTP/SSL Listener starting up")
    } else {
      logger.info("SMTP Listener starting up")
    }

		listenSocket = Some(listen())
  }

  override def postStop: Unit = {
    super.postStop
    logger.info("SMTP Listener has been stopped")
  }

  override def receive: Receive = {
		case AcceptConnections => {
			dispatcher = Some(sender())

			listenSocket.foreach(socket => {
				socket.bind(new InetSocketAddress("0.0.0.0", config.listenPort))

				Stream.continually(listenSocket)
					.filter(_.isDefined)
					.map(_.get)
					.map(_.accept())
					.takeWhile(_ => true)
					.foreach(clientSocket => dispatcher.foreach(dp => {
						context.actorSelection("/user/statService") ! IncrementCounter("smtp.connections")
						dp ! ClientConnected(clientSocket)
					}))
			})
		}
		case ClientQuit => logger.info("Client disconnected")
    case _ => logger.warning("SMTPListener has received an unknown message")
  }

	def listen(): ServerSocket = {
		val socket = if (config.ssl) {
			createTLSListenSocket
		} else {
			createListenSocket
		}

		socket
	}

	def createTLSListenSocket: ServerSocket = {
		val context = SSLContext.getInstance(TLS_VERSION)
		context.init(Array(), Array(new PermissiveTrustManager()), new SecureRandom())

		val socketFactory = context.getServerSocketFactory
		val socket = socketFactory.createServerSocket.asInstanceOf[SSLServerSocket]
		socket.setUseClientMode(false)
		socket
	}

	def createListenSocket: ServerSocket = {
		val socketFactory = ServerSocketFactory.getDefault
		socketFactory.createServerSocket
	}
}

object SMTPListener {

  /**
    * Provide convenient access to Props for this type
    *
    * @return a Props for creating an SMTP listener with reasonable default values
    */
  def props(config: SMTPConfig): Props = Props(new SMTPListener(config))

}

case object AcceptConnections
case class ClientConnected(remote: Socket)
case object ClientQuit
