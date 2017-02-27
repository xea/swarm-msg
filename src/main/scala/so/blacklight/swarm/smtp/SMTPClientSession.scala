package so.blacklight.swarm.smtp

import java.io._
import java.net.Socket

import akka.actor.{Actor, Props}
import akka.event.Logging

import scala.util.matching.Regex

/**
	* Represents a low-level SMTP client session that can send and receive SMTP protocol messages.
	*
	* @param clientSocket client's network socket
	*/
class SMTPClientSession(clientSocket: Socket) extends Actor {

	val reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))
	val writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream))
	val logger = Logging(context.system, this)

	private val trailingDot: String = "\r\n.\r\n"

	def send(msg: SMTPEvent): Unit = {
		msg match {
			case clientMsg: SMTPClientEvent =>
				sendClientEvent(clientMsg)
			case serverMsg: SMTPServerEvent =>
				sendServerEvent(serverMsg)
			case _ => ()
		}
	}

	def sendClientEvent(msg: SMTPClientEvent): Unit = {
		msg match {
			case SMTPClientEhlo(hostId) => writeln(SMTPCommand.ehlo(hostId))
			case SMTPClientMailFrom(sender) => writeln(SMTPCommand.mailFrom(sender))
			case SMTPClientReceiptTo(recipient) => writeln(SMTPCommand.receiptTo(recipient))
			case SMTPClientDataBegin => writeln(SMTPCommand.data)
			case SMTPClientDataEnd(messageBody) =>
				write(messageBody)
				writeln(trailingDot)
			case SMTPClientQuit => writeln(SMTPCommand.quit)
			case ClientDisconnected => closeConnection()
		}
	}

	def sendServerEvent(msg: SMTPServerEvent): Unit = {
		msg match {
			case SMTPServerGreeting(greeting) => writeln(SMTPReplyMessages.connectOk(greeting))
			case SMTPServerEhlo(capabilities) =>
				capabilities.init.foreach(capability => writeln(s"250-$capability"))
				Option(capabilities.last).foreach(capability => writeln(s"250 $capability"))
			case SMTPServerDataReady => writeln(SMTPReplyMessages.dataReady)
			case SMTPServerDataOk => writeln(SMTPReplyMessages.dataOk)
			case SMTPServerOk => writeln(SMTPReplyMessages.ok)
			case SMTPServerQuit => writeln(SMTPReplyMessages.ok)
			case SMTPServerSyntaxError => writeln(SMTPReplyMessages.commandNotRecognised)
			case SMTPServerInvalidParameter => writeln(SMTPReplyMessages.invalidArgument)
			case SMTPServerBadSequence => writeln(SMTPReplyMessages.badSequence)
			case other => logger.warning(s"Received unknown message request: $other")
		}
	}

	/**
		* Events handled here will be interpreted as interactions going out to the clients, eg. returning
		* command replies, expecting user input or closing the connection.
		*
		* @return
		*/
	override def receive: Receive = {
		case msg @ SMTPServerDataReady =>
			send(msg)
			sender() ! readData()
		case msg @ SMTPServerQuit =>
			send(msg)
			closeConnection()
		case ClientDisconnected =>
			closeConnection()
		// If the message does not have a specific handler, then we'll just try and send it to the client
		// and expect a one-line reply
		case msg: SMTPEvent =>
			send(msg)
			sender() ! readReply()
		case otherMsg =>
			logger.warning(s"Unknown message found: $otherMsg")
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
			case SMTPPattern.noop() => SMTPClientNoOperation
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
	def readData(): SMTPClientEvent = {

		try {
			// Explanation: create an infinite stream of functions of unevaluated read lines first
			val result = Stream.continually(() => reader.readLine())
				// Call the functions and thus read a line on-demand
				.map(_())
				// Wrap the result in an Option
				.map(Option(_))
				// When a None is read then we hit EOF, stop processing
			  .takeWhile(_.isDefined)
				// In order to make the tailing dot part of the message, we look for Nones we put an
				// extra None after it
				.flatMap(_ match {
					case Some(".") => List(Some("."), None)
					case other => List(other)
				})
				// At this point the there should be a None immediately after the trailing dot
			  .takeWhile(_.isDefined)
				.foldLeft((true, new ByteArrayOutputStream()))((acc, x) => acc match {
					case (true, buffer) => x match {
						case Some(".") => (false, buffer)
						case Some(line) =>
							buffer.write(line.getBytes())
							(true, buffer)
						// The below should never happen as None values are filtered out in the above takeWhile
						case _ => (true, buffer)
					}
					case other => other
				})

			result match {
				// If the transmission ended without a trailing dot, then we consider this a disconnect
				case (true, _) => ClientDisconnected
				case (false, buffer) => SMTPClientDataEnd(new String(buffer.toByteArray).toCharArray)
			}
		} catch {
			// On any read IO error, we disconnect
			case _: IOException => ClientDisconnected
		}
	}

	/**
		* Assume that the SMTP transmission has been finished (either abruptly or normally)
		* and close the connection.
		*/
	def closeConnection(): Unit = {
		logger.info(s"Closing connection $clientSocket")
		writer.close()
		reader.close()
		clientSocket.close()
	}

	/**
		* Write a message to the client's output stream and append a new line 'r\n' at the end
		*
		* @param msg message to be sent as a character array
		* @param flush output buffer flush required?
		*/
	protected def writeln(msg: Array[Char], flush: Boolean): Unit = {
		write(msg ++ Array('\r', '\n'), flush)
	}

	/**
		* Write a message to the client's output stream and append a new line '\r\n' at the end.
		*
		* @param msg message to send
		* @param flush output buffer flush required?
		*/
	protected def writeln(msg: String, flush: Boolean = true): Unit = {
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
	val noop: Regex = "^(?i)NOOP$".r
	val reset: Regex = "^(?i)RSET$".r
}

object SMTPCommand {
	def ehlo(hostId: String): String = s"EHLO $hostId"
	def mailFrom(sender: String): String = s"MAIL FROM: sender"
	def receiptTo(recipient: String): String = s"RCPT TO: recipient"
	def data: String = "DATA"
	def customCommand(cmd: String): String = cmd
	def quit: String = "QUIT"

}

object SMTPReplyMessages {

	// Some generic responses
	def commandNotRecognised: String = "500 Syntax error, command not recognised"
	def invalidArgument: String = "501 Syntax error in parameters or arguments"
	def badSequence: String = "503 Bad sequence of commands"
	def notImplemented: String = "550 Not implemented"
	def paramNotImplemented: String = "504 Parameter not implemented"

	def ok: String = "250 OK"

	// Connection initial reply codes
	def connectOk(domain: String): String = s"220 $domain service ready"
	def connectNotAvailable(domain: String): String = s"421 $domain service not available, closing channel"

	// EHLO/HELO reply codes
	def ehloOk(domain: String): String = s"250 Welcome $domain"

	// DATA reply codes
	def dataReady: String = "354 Start mail input, end with <CRLF>.<CRLF>"
	def dataOk: String = "250 Mail accepted"
}
