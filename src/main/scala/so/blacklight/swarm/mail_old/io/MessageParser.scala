package so.blacklight.swarm.mail_old.io

import java.io.PushbackInputStream
import java.nio.ByteBuffer
import java.nio.channels.{Channels, ReadableByteChannel}

class MessageParser {

	val MAX_LINE_SIZE: Int = 1500

	def parseStream(input: PushbackInputStream, parser: Parser): Unit = {
		implicit val lineBuffer = ByteBuffer.allocate(MAX_LINE_SIZE)

		val readChannel = Channels.newChannel(input)

	}

	def fillBuffer(implicit lineBuffer: ByteBuffer, channel: ReadableByteChannel): ByteBuffer = {
		lineBuffer

	}

	def endOfMessageReached(): Boolean = {
		true
	}

}

class Parser {

}
