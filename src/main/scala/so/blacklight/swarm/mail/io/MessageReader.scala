package so.blacklight.swarm.mail.io

import java.io.{ByteArrayOutputStream, DataInputStream, InputStream, OutputStream}
import java.nio.{ByteBuffer, CharBuffer}

import so.blacklight.swarm.mail.{Header, Message, MimePart, RawMessage}

import scala.io.Source

trait MessageReader[+T <: Message] {

	def fromInputStream(inputStream: InputStream): T

}

/*
Unicode new lines:
 LF:    Line Feed, U+000A
 VT:    Vertical Tab, U+000B
 FF:    Form Feed, U+000C
 CR:    Carriage Return, U+000D
 CR+LF: CR (U+000D) followed by LF (U+000A)
 NEL:   Next Line, U+0085
 LS:    Line Separator, U+2028
 PS:    Paragraph Separator, U+2029
 */

class RawMessageReader extends MessageReader[RawMessage] {

	private val trailingDot = Array('\r', '\n', '.', '\r', '\n')

	override def fromInputStream(inputStream: InputStream): RawMessage = {
		implicit val buffer = ByteBuffer.allocate(trailingDot.length);
		implicit val output = new ByteArrayOutputStream()

		Stream.continually { inputStream.read() }
			.map { _.asInstanceOf[Byte] }
			.takeWhile { hasAvailable }
			.takeWhile { hasContent }
			.foreach { output.write(_) }

		new RawMessage(output.toByteArray)
	}

	// Simple check to see if the last read byte was EOF or an actual value
	private def hasAvailable(char: Byte): Boolean = char >= 0

	// So, what happens here is that we maintain a short internal buffer to keep track of trailing
	// dots indicating the end of message. Whenever we find a potential follow-up, we build up the
	// buffer until we really hit the end of message or turns out to be a false positive and we clear the buffer
	private def hasContent(char: Byte)(implicit buffer: ByteBuffer, output: OutputStream): Boolean = {
		val bufferPos = buffer.position()

		// If the current character matches the expected position and character, we increment the buffer
		if (bufferPos < trailingDot.length - 1 && char == trailingDot(bufferPos)) {
			buffer.put(char)
			true

		// If we hit the last character then we reject further reads
		} else if (bufferPos == trailingDot.length - 1 && char == trailingDot.last) {
			output.write(char)
			buffer.clear()
			false

		// Otherwise carry on reading
		} else {

			// Clear false positives
			if (bufferPos > 0) {
				buffer.clear()
			}

			true
		}
	}
}


/**
	* RawMessageReader takes an arbitrary limited byte stream, reads every byte from the
	* stream and stores the read bytes, preserving all the bytes in their original state.
	*/
class RawMessageReader_ extends MessageReader[RawMessage] {

	def fromInputStream(inputStream: InputStream): RawMessage = {
		val buffer = new ByteArrayOutputStream()
		val contents = Source.fromInputStream(inputStream).buffered.foldLeft(buffer)((acc, x) => {
			acc.write(x)
			acc
		})
		new RawMessage(Array())//(contents.toByteArray)
	}
	private def lookForEnd(c: Char)(implicit lineCount: Int, buffer: CharBuffer): List[Option[Char]] = {
		if (lineCount > 0 && c == '\n' && (buffer.position() == 1 && buffer.get(0) == '.') || (buffer.position() == 2 && buffer.get(0) == '.' && buffer.get(1) == '\r')) {
			List(Some(c), None)
		} else {
			List(Some(c))
		}
	}

	private def bufferEndOfMessage(c: Char)(implicit buffer: CharBuffer): Boolean = {
		c == '\n' && (buffer.position() == 1 && buffer.get(0) == '.') || (buffer.position() == 2 && buffer.get(0) == '.' && buffer.get(1) == '\r')
	}


	private def handleCharacter(lines: List[String], character: Char)(implicit buffer: CharBuffer): List[String] = {
		buffer.put(character)

		println(s"Character $character")

		if (character == '\n') {
			val newLine: String = buffer.flip().toString

			buffer.clear()

			lines :+ newLine
		} else {
			lines
		}
	}
}

class MimePartReader[T <: MimePart] {

	def readHeaders(): Either[ParseError, Seq[Header]] = {
		Right(List())
	}
}

class ComposingMimePartReader[T <: MimePart] extends MimePartReader[T] {

	def parse(): Either[ParseError, T] = {
		Left(GenericError)
	}

}

sealed trait ParseError
case object GenericError extends ParseError
