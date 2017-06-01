package so.blacklight.swarm.email

import java.io.File

import org.scalatest.FunSpec

/**
	*
	*/
class MessageSpec extends FunSpec {

	describe("RawMessage") {
		it ("asdf") {
			val startTime = System.currentTimeMillis()
			val m = new FileContent(new File("input2.txt"))

			//m.getContent foreach(println)
			val result = m.getContent.length

			val totalTime = System.currentTimeMillis() - startTime

			println(result)
			println(totalTime)
		}
	}
}
