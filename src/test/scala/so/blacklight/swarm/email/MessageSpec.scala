package so.blacklight.swarm.email

import java.io.ByteArrayInputStream

import org.scalatest.FunSpec

/**
	*
	*/
class MessageSpec extends FunSpec {

	describe("RawMessage") {
		it ("asdf") {
			val inputStream = new ByteArrayInputStream(Array[Byte](9, 1, 2, 3, 4))

			val reader = () => {
				inputStream.read().asInstanceOf[Byte]
			}

			lazy val out: Stream[Byte] = Stream.continually(reader).map(_())

		}
	}
}
