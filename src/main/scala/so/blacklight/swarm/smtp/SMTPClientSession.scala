package so.blacklight.swarm.smtp

import java.io.{BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter}
import java.net.Socket

import akka.actor.{Actor, Props}

import scala.util.matching.Regex

/**
	* Represents a low-level SMTP client session that can send and receive SMTP protocol messages.
	*
	* @param clientSocket client's network socket
	*/
class SMTPClientSession(clientSocket: Socket) extends Actor {

	val reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))
	val writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream))

	def send(msg: Any): Unit = {
		msg match {
			case SMTPServerGreeting(greeting) => writeln(SMTPReplyMessages.connectOk(greeting))
			case SMTPServerEhlo(capabilities) =>
				capabilities.init.foreach(capability => writeln(s"250-$capability"))
				Option(capabilities.last).foreach(capability => writeln(s"250 $capability"))
			case SMTPServerDataOk => writeln(SMTPReplyMessages.dataReady)
			case SMTPServerOk => writeln("250 OK")
			case SMTPServerQuit => writeln("250 OK")
			// TODO handle unsupported protocol messages
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
		case msg =>
			send(msg)
			sender() ! readReply()
	}

	/**
		* Wait for a reply message from the client and translate it to an actor messages
		*
		* @return client's message
		*/
	def readReply(): SMTPClientEvent = {
		val line = reader.readLine.trim

		line match {
			case SMTPPattern.ehlo(hostId) => SMTPClientEhlo(hostId)
			case SMTPPattern.mailFrom(sender) => SMTPClientMailFrom(sender)
			case SMTPPattern.rcptTo(recipient) => SMTPClientReceiptTo(recipient)
			case SMTPPattern.data() => SMTPClientDataBegin
			case SMTPPattern.quit() => SMTPClientQuit
			case _ => SMTPClientUnknownCommand
		}
	}

	/**
		* Read a multi-line reply from the client sent in response to a "DATA OK" acknowledgement.
		*
		* The reply is expected to end with a "\n.\n" sequence.
		*
		* @return client's message body
		*/
	def readData(): SMTPClientDataEnd = {
		// TODO return an Either object representing a possible error
		val msg = Stream.continually(() => reader.readLine())
			.map(f => f())
			.takeWhile(s => !s.equals("."))
		  .mkString("")

		SMTPClientDataEnd(msg)
	}

	/**
		* Write a message to the client's output stream and append a new line '\r\n' at the end.
		*
		* @param msg message to be sent
		* @param flush output buffer flush required?
		*/
	protected def writeln(msg: String, flush: Boolean = true): Unit = {
		val dontFlush = false

		write(msg.toCharArray ++ Array('\r', '\n'), flush)
	}

	/**
		* Send a message to the socket's output stream and flush the output buffer if
		* required.
		*
		* @param msg a character array message
		* @param flush output buffer flush required?
		*/
	protected def write(msg: Array[Char], flush: Boolean = true): Unit = {
		writer.write(msg)

		print(s"> ${msg.mkString}")

		if (flush) {
			writer.flush()
		}
	}

}

object SMTPClientSession {
	def props(clientSocket: Socket): Props = Props(SMTPClientSession(clientSocket))
	def apply(clientSocket: Socket): SMTPClientSession = new SMTPClientSession(clientSocket)
}

/**
	* Defines a collection of regular expressions that match typical SMTP protocol commands
	*/
object SMTPPattern {
	val helo: Regex = "^(?i)HELO\\s+(.*)\\s*$".r
	val ehlo: Regex = "^(?i)EHLO\\s+(.*)\\s*$".r
	val mailFrom: Regex = "^(?i)MAIL FROM:\\s*(.*)\\s*$".r
	val rcptTo: Regex = "^(?i)RCPT TO:\\s*(.*)\\s*$".r
	val data: Regex = "^(?i)DATA$".r
	val quit: Regex = "^(?i)QUIT$".r
	val reset: Regex = "^(?i)RSET$".r
}

object SMTPReplyMessages {
	// Connection initial reply codes
	def connectOk(domain: String): String = s"220 $domain service ready"
	def connectNotAvailable(domain: String): String = s"421 $domain service not available, closing channel"

	// EHLO/HELO reply codes
	def ehloOk(domain: String): String = s"250 Welcome $domain"
	def ehloSyntax: String = "500 Syntax error"
	def ehloSyntaxParams: String = "501 Syntax error in parameters"

	// DATA reply codes
	def dataReady: String = "451 Start mail input, end with <CRLF>.<CRLF>"
	def dataOk: String = "250 Mail accepted"
}
