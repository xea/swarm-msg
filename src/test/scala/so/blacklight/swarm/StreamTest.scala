package so.blacklight.swarm

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.ByteBuffer
import java.nio.channels.{Channels, ReadableByteChannel}


/**
	*
	*/
class StreamTest {

	val LINE_BUFFER_SIZE: Int = 6

	def from(inputStream: InputStream): Unit = {
		implicit val readBuffer = ByteBuffer.allocate(LINE_BUFFER_SIZE)
		implicit val channel = Channels.newChannel(inputStream)

		val result = Stream
			.continually(() => fillBuffer)
			.map { _() }
			.map(splitLines)
			.takeWhile(lines => {
				lines.length > 0
			})
			.foreach(lines => {
				println(lines.length)
			})
//			.map(processLines)
//			.takeWhile(a => true)

	}

	def fillBuffer(implicit channel: ReadableByteChannel, buffer: ByteBuffer): Int = {
		println("--------")
		val readCount = channel.read(buffer)
		println(s"Refilling buffer with $readCount")
		readCount
	}

	def processLines(newLines: List[Array[Byte]]): Boolean = {
		println("Processing new lines")

		true
	}

	def splitLines(readCount: Int)(implicit buffer: ByteBuffer): List[Array[Byte]] = {
		println(s"Splitting lines")

		val lineBuffer: ByteBuffer = ByteBuffer.allocate(LINE_BUFFER_SIZE)

		buffer.mark()
		buffer.flip()

		val result = buffer.array().foldLeft(List[Array[Byte]]())((acc, x) => {
			lineBuffer.put(x)

			val c = x.r

			println(s" char $c")

			x match {
				case ' ' =>
					val thisLine = Array.fill[Byte](lineBuffer.position()) { 0 }
					lineBuffer.flip()
					lineBuffer.get(thisLine).clear()
					println(s"   Got new line of length ${thisLine.length}")
					acc :+ thisLine
				case _ =>
					acc
			}
		})

		buffer.clear()

		result
	}

}

object StreamTest extends App {


	val input = "a"
	val inputStream = new ByteArrayInputStream(input.getBytes)

	val test = new StreamTest
	test.from(inputStream)


	/*
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
		*/


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
