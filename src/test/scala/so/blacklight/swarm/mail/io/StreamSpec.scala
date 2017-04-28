package so.blacklight.swarm.mail.io

import java.io.{ByteArrayInputStream, InputStreamReader}

import org.scalatest.{FlatSpec, FunSpec}

import scala.io.Source

/**
	*
	*/
class StreamSpec extends FunSpec {


	describe("reading from input streams does not read more than necessary amount of bytes") {
		val stream = new ByteArrayInputStream("1234567890qwertyuiopasdfghjklzxcvbnmm".getBytes)

		val reader = new InputStreamReader(stream)
		Source.fromInputStream(stream).takeWhile(i => i != 0).foreach(println)


	}


}
