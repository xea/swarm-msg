package so.blacklight.swarm.actortest

import java.io.IOException

import scala.util.Random

/**
	*
	*/
class IOSimulator {

	private val maxArrayLength: Int = 1024

	def throwsException(): Array[Byte] = {
		throw new IOException("Requested throw")
	}

	def mayThrowException(probability: Double): Array[Int]  = {
		if (Random.nextDouble() > probability) {
			val length = Random.nextInt(maxArrayLength)

			Array.range(0, length)
		} else {
			throw new IOException("Requested exception")
		}
	}

	def mayThrowRuntimeException(probability: Double): Array[Int] = {
		if (Random.nextDouble() > probability) {
			val length = Random.nextInt(maxArrayLength)

			Array.range(0, length)
		} else {
			throw new RuntimeException("Requested Runtime Exception")
		}
	}

}
