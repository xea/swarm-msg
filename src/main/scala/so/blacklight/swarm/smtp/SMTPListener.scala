package so.blacklight.swarm.smtp

import java.net.InetSocketAddress

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.io.Tcp.{Bind, Bound, CommandFailed, Connected}
import akka.io.{IO, Tcp}

/**
  */
class SMTPListener(config: SMTPConfig) extends Actor {

  val logger = Logging(context.system, this)

  IO(Tcp) ! Bind(self, new InetSocketAddress("0.0.0.0", config.listenPort))

  override def preStart = {
    super.preStart

    if (config.ssl) {
      logger.info("SMTP/SSL Listener starting up")
    } else {
      logger.info("SMTP Listener starting up")
    }
  }

  override def postStop = {
    super.postStop
    logger.info("SMTP Listener has been stopped")
  }

  override def receive: Receive = {
    case b @ Bound(localAddress) => ()
    case c @ Connected(remote, local) => ()
    case CommandFailed(_: Bind) => context stop self
    case _ => logger.warning("SMTPListener has received an unknown message")
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
