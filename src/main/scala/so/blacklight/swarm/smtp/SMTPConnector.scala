package so.blacklight.swarm.smtp

import java.io.{BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter}
import java.net.Socket

import akka.actor.Actor
import akka.event.Logging

/**
  *
  */
class SMTPConnector extends Actor {

  val logger = Logging(context.system, this)

  override def receive: Receive = {
    case ClientConnected(clientSocket) => processConnection(clientSocket)
    case _ => println("Received message")
  }

  def processConnection(clientSocket: Socket) = {
    logger.info(s"Processing connection from ${clientSocket.getRemoteSocketAddress.toString}")

    val client = SMTPClientSession(clientSocket)
    client.greet
  }

}

class SMTPClientSession(clientSocket: Socket) {

  val reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))
  val writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream))

  def greet = {
    writer.write("220 Swarm SMTP\r\n")
    writer.flush()
  }

}

object SMTPClientSession {

  def apply(clientSocket: Socket): SMTPClientSession = new SMTPClientSession(clientSocket)
}
