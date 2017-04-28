package so.blacklight.swarm.mail.io

import java.io.ByteArrayInputStream
import java.nio.CharBuffer

import org.scalatest.FunSpec

/**
	*
	*/
class MessageReaderSpec extends FunSpec {

	describe("RawMessageReader") {
		it ("should treat empty input as empty messages") {
			val input = new ByteArrayInputStream(Array())

			val reader = new RawMessageReader

			val message = reader.fromInputStream(input)

			assert(message.length == 0)
		}

		it ("should terminate messages at the trailing dot character") {
			val realMessage = "message body \r\n.\r\n"
			val extraMessage = "extra data"
			val data = s"$realMessage$extraMessage"
			val input = new ByteArrayInputStream(data.getBytes)
			val reader = new RawMessageReader
			val message = reader.fromInputStream(input)

			assert(message.length == realMessage.length)
		}

		it ("should read multiple message from the same stream") {
			def message(id: Int): String = s"message body $id\r\n.\r\n"

			val data = s"${message(0)}${message(1)}${message(2)}${message(3)}"
			val input = new ByteArrayInputStream(data.getBytes())
			val reader = new RawMessageReader
			val message0 = reader.fromInputStream(input)
			val message1 = reader.fromInputStream(input)
			val message2 = reader.fromInputStream(input)
			val message3 = reader.fromInputStream(input)

			assert(message0.length == message(0).length)
			assert(message1.length == message(1).length)
			assert(message2.length == message(2).length)
			assert(message3.length == message(3).length)
		}
	}

	describe("BufferedReader") {

		it ("should do stuff") {
			val buffer = CharBuffer.allocate(1000)

			buffer.put("Alma")

			assert(buffer.position() == 4)
			assert(buffer.limit() == 1000)
			assert(buffer.remaining() == 996)
			assert(buffer.capacity() == 1000)

			buffer.rewind()

			assert(buffer.position() == 0)
			assert(buffer.limit() == 1000)
			assert(buffer.remaining() == 1000)
			assert(buffer.capacity() == 1000)
		}

	}
}
