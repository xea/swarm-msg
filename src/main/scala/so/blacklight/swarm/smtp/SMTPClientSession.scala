package so.blacklight.swarm.smtp

import java.io.{BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter}
import java.net.Socket

import akka.actor.{Actor, Props}

/**
	*
	*/
class SMTPClientSession(clientSocket: Socket) extends Actor {

	val reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
	val writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))

	def send(msg: Any): Unit = {
		val alwaysFlush = true

		msg match {
			case SMTPServerGreeting(greeting) => writeln(s"220 $greeting")
			case SMTPServerEhlo(capabilities) => {
				capabilities.init.foreach(capability => writeln(s"250-$capability"))
				Option(capabilities.last).map(capability => writeln(s"250 $capability"))
			}
			case SMTPServerDataOk => {
				writeln(s"341 OK")
			}
			case SMTPServerOk => writeln("250 OK")
			case SMTPServerQuit => writeln("250 OK")
		}
	}

	override def receive: Receive = {
		case msg @ SMTPServerDataOk =>
			send(msg)
			sender() ! readData()
		case msg @ SMTPServerQuit =>
			send(msg)
			writer.close()
			reader.close()
			clientSocket.close()
		case msg => {
			send(msg)
			sender() ! readReply()
		}
	}

	def readReply(): SMTPClientEvent = {
		val line = reader.readLine.trim

		line match {
			case SMTPPattern.ehlo(hostId) => SMTPClientEhlo(hostId)
			case SMTPPattern.mailFrom(sender) => SMTPClientMailFrom(sender)
			case SMTPPattern.rcptTo(recipient) => SMTPClientReceiptTo(recipient)
			case SMTPPattern.data() => {
				SMTPClientDataBegin
			}
			case SMTPPattern.quit() => SMTPClientQuit
			case _ => SMTPClientUnknownCommand
		}
	}

	def readData(): SMTPClientDataEnd = {
		SMTPClientDataEnd("User contents")
	}

	protected def writeln(msg: String, flush: Boolean = true): Unit = {
		val dontFlush = false

		write(msg, dontFlush)
		write("\r\n", flush)
	}

	protected def write(msg: String, flush: Boolean = true): Unit = {
		writer.write(msg)

		if (flush) {
			writer.flush()
		}
	}

}

object SMTPClientSession {
	def props(clientSocket: Socket): Props = Props(SMTPClientSession(clientSocket))
	def apply(clientSocket: Socket): SMTPClientSession = new SMTPClientSession(clientSocket)
}

object SMTPPattern {
	val ehlo = "^(?i)EHLO\\s+(.*)\\s*$".r
	val mailFrom = "^(?i)MAIL FROM:\\s*(.*)\\s*$".r
	val rcptTo = "^(?i)RCPT TO:\\s*(.*)\\s*$".r
	val data = "^(?i)DATA$".r
	val quit = "^(?i)QUIT$".r
}

