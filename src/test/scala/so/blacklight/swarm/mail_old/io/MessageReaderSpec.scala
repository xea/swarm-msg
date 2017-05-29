package so.blacklight.swarm.mail_old.io

import java.io.ByteArrayInputStream

import org.scalatest.FunSpec

/**
	*
	*/
class MessageReaderSpec extends FunSpec {

	describe("RawMessageReader") {
		it ("should treat empty input as empty messages") {
			val input = new ByteArrayInputStream(Array())

			val reader = new RawMessageReader

			val message = reader.from(input)

			assert(message.isRight)
			assert(message.right.get.length == 0)
		}

		it ("should terminate messages at the trailing dot character") {
			val realMessage = "message body \r\n.\r\n"
			val extraMessage = "extra data"
			val data = s"$realMessage$extraMessage"
			val input = new ByteArrayInputStream(data.getBytes)
			val reader = new RawMessageReader
			val message = reader.from(input)

			assert(message.isRight)
			assert(message.right.get.length == realMessage.length)
		}

		it ("should read multiple message from the same stream") {
			def message(id: Int): String = s"message body $id\r\n.\r\n"

			val data = s"${message(0)}${message(1)}${message(2)}${message(3)}"
			val input = new ByteArrayInputStream(data.getBytes())
			val reader = new RawMessageReader
			val message0 = reader.from(input)
			val message1 = reader.from(input)
			val message2 = reader.from(input)
			val message3 = reader.from(input)

			assert(message0.isRight)
			assert(message0.right.get.length == message(0).length)
			assert(message1.isRight)
			assert(message1.right.get.length == message(1).length)
			assert(message2.isRight)
			assert(message2.right.get.length == message(2).length)
			assert(message3.isRight)
			assert(message3.right.get.length == message(3).length)
		}
	}
}
