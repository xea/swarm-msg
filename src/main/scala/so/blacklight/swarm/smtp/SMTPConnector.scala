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
    //case SMTPClientEhlo(_) => sender ! SMTPServerEhlo(Array("LOFASZ", "FISH"))
    case _ => println("Received message")
  }

  def processConnection(clientSocket: Socket): Unit = {
    logger.info(s"Processing connection from ${clientSocket.getRemoteSocketAddress.toString}")

    val clientSession = SMTPClientSession(clientSocket)
    val protocolHandler = context.actorOf(SMTPProtocolHandler.props(clientSession), "protocolHandler")

    protocolHandler ! SMTPServerGreeting("Swarm Server")
  }

}

class SMTPClientSession(clientSocket: Socket) {

  val reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))
  val writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream))

  /*
  def greet = {
    writer.write("220 Swarm SMTP\r\n")
    writer.flush()
  }
  */
  def send(msg: Any): Unit = {
		val alwaysFlush = true

    msg match {
      case SMTPServerGreeting(greeting) => writeln(s"220 $greeting", alwaysFlush)
    }
  }

  def readReply(): SMTPClientEvent = {
		val line = reader.readLine.trim.toUpperCase

		line match {
			case "QUIT" => SMTPClientQuit
			case _ => SMTPClientUnknownCommand
		}
  }

  protected def writeln(msg: String, flush: Boolean): Unit = {
		val dontFlush = false

    write(msg, dontFlush)
    write("\r\n", flush)
  }

  protected def write(msg: String, flush: Boolean): Unit = {
    writer.write(msg)

    if (flush) {
      writer.flush()
    }
  }
}

object SMTPClientSession {

  def apply(clientSocket: Socket): SMTPClientSession = new SMTPClientSession(clientSocket)
}
