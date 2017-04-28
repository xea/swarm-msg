package so.blacklight.swarm.mail.io

import java.io.{ByteArrayOutputStream, DataInputStream, InputStream}
import java.nio.CharBuffer

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
	override def fromInputStream(inputStream: InputStream): RawMessage = {
		implicit val buffer = CharBuffer.allocate(1500)
		implicit var lineCount = 0

		var i: Int = 0

		val dis = new DataInputStream(inputStream)

		val a = Stream.continually { dis.read() }
			.map { _.asInstanceOf[Byte] }
			.takeWhile { hasAvailable }
			.map { fullBuffer }


		val lines = List()
		new RawMessage(lines)
	}

	private def fillBuffer(byte: Byte): Byte = {

		byte
	}

	private def hasAvailable(char: Byte): Boolean = char >= 0

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
		new RawMessage(List())//(contents.toByteArray)
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
