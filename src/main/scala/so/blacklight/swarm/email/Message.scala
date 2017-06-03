package so.blacklight.swarm.email

import java.io.{BufferedInputStream, File, FileInputStream, InputStream}
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption

import scala.util.Try

trait Message[+T] {
	def getContent: Stream[T]
}

trait MimePart {

}

trait MultiPart extends MimePart {
	def getParts: Stream[MimePart]
}



/*
abstract class InputStreamContent(inputStream: InputStream) extends Message[Byte] {

	private def getDirectContent(is: InputStream): Stream[Byte] = {
		Stream continually { () => is.read() } map { _() } takeWhile(_ > -1) map { _.asInstanceOf[Byte] }
	}

	protected def getBufferedContent: Stream[Byte] = getDirectContent(new BufferedInputStream(inputStream))

}

class BufferedFileContent(file: File) extends InputStreamContent(new FileInputStream(file)) {
	override def getContent: Stream[Byte] = getBufferedContent
}

class MappedFileContent(file: File) extends Message[Byte] {

	private val BUFFER_SIZE: Int = 65536

	override def getContent: Stream[Byte] = {
		val mappedBuffer = FileChannel.open(file.toPath, StandardOpenOption.READ)
		val readBuffer = ByteBuffer.allocate(BUFFER_SIZE)
		readBuffer.flip()

		def refillBuffer = {
			readBuffer.clear()
			mappedBuffer.read(readBuffer)
			readBuffer.flip()
		}

		def isBufferEmpty = readBuffer.remaining() < 1

		def readByte: Try[Byte] = {
			if (isBufferEmpty) {
				refillBuffer
			}

			Try(readBuffer.get)
		}

		Stream continually { () => readByte } map { _() } takeWhile { _.isSuccess } map { _.get }
	}

}
*/

/*
class RawMessage(content: Stream[Byte]) extends Message[Byte] {
	override def getContent: Stream[Byte] = content
}

object RawMessage {
	def apply(bytes: Array[Byte]): RawMessage = {
		new RawMessage(Stream(bytes:_*))
	}

	def apply(inputStream: InputStream): RawMessage = {
	}
}

trait MimeMessage extends Message[Byte] {

	def getHeaders: Seq[MimeHeader]

	def getSubject: Option[String]

	def getFrom: Option[String]

	def getTo: Option[String]

}

trait Header[+K, +V] {

	def getHeader: K

	def getValue: V

}

class MimeHeader(header: String, value: String) extends Header[String, String] {

	override def getHeader: String = header

	override def getValue: String = value

}

class RFC2822Message(headers: Seq[MimeHeader]) extends MimeMessage {

	protected def getHeader(header: String): Option[MimeHeader] = getHeaders find { _.getHeader.toLowerCase == header.toLowerCase }

	protected def getHeaderValue(header: String): Option[String] = getHeader(header) map { _.getValue }

	override def getHeaders: Seq[MimeHeader] = headers

	override def getSubject: Option[String] = getHeaderValue("Subject")

	override def getFrom: Option[String] = getHeaderValue("From")

	override def getTo: Option[String] = getHeaderValue("To")

	override def getContent: Stream[Byte] = ???
}
*/
