package so.blacklight.swarm

import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.channels.{Channels, ReadableByteChannel}


/**
	*
	*/
class StreamTest {


}

object StreamTest extends App {

	val values = Array[Byte](1, 2, 3, 0, 4, 5, 6, 7, 0, 8, 9, 10, 11, 12, 13, 14, 15, 16, 0)
	implicit val buffer = ByteBuffer.allocate(8)
	implicit val channel = Channels.newChannel(new ByteArrayInputStream(values))

	def fillBuffer(implicit buffer: ByteBuffer, channel: ReadableByteChannel): ByteBuffer = {
		println("Fill buffer")
		channel.read(buffer)
		buffer
	}

	Stream.continually { fillBuffer }
		.flatMap(buffer => {
			println("Split lines")
			buffer.flip()

			val tmp = Array.fill[Byte](8) { 0 }

			if (buffer.position() > 0) {
				buffer.get(tmp)

				buffer.clear

				List[Seq[Byte]](List(tmp(0)))
			} else {
				List[Seq[Byte]]()
			}
		})


	/*
	Stream(1) match {
		case a #:: b #:: c => println("Three")
		case a #:: b => println("Two")
		case a => println("One")
	}
	*/

	/*
	var i = 0
	println(Stream.continually(() => { i = i + 2; i }).map(f => f()).take(3).toList)

	i = 0

	println(Stream.continually(() => { i = i + 1; i }).map(f => f()).takeWhile(_ <= 4).toList)
	println(i)
	*/

}
