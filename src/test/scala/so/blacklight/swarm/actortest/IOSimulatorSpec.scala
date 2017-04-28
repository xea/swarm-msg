package so.blacklight.swarm.actortest

import java.io.IOException

import org.scalatest.FunSpec

/**
	*
	*/
class IOSimulatorSpec extends FunSpec {
	describe("throwsException") {
		it ("Should always throw an exception") {
			val simulator = new IOSimulator

			assertThrows[IOException] {
				simulator.throwsException()
			}
		}
	}

	describe("mayThrowException") {
		describe("when probability is 0.0") {
			it("should never throw an exception") {
				val simulator = new IOSimulator

				0.to(1000).foreach((_) => {
					val _ = simulator.mayThrowException(0.0)

					0
				})

				assert(true)
			}
		}

		describe("when probability is 1.0") {
			it ("should always throw an exception") {
				val simulator = new IOSimulator

				0.to(1000).foreach((_) => {
					assertThrows[IOException] {
						val _ = simulator.mayThrowException(1.0)
					}
				})
			}
		}

		describe("when probability is between 0.0 and 1.0") {
			it ("should throw exceptions in an expected amount") {
				val simulator = new IOSimulator

				def runTest(totalRuns: Int, delta: Double, rate: Double) = {
					var exceptionCount: Int = 0

					0.to(totalRuns).foreach((_) => {
						try {
							val _ = simulator.mayThrowException(rate)
						} catch {
							case _ => exceptionCount += 1
						}
					})

					val ideal = totalRuns * rate
					val minAccepted = ideal - (totalRuns * delta)
					val maxAccepted = ideal + (totalRuns * delta)

					assert(exceptionCount > minAccepted && exceptionCount < maxAccepted)
				}

				runTest(10000, 10.0, 0.1)
				runTest(10000, 10.0, 0.2)
				runTest(10000, 10.0, 0.3)
				runTest(10000, 10.0, 0.4)
				runTest(10000, 10.0, 0.5)
				runTest(10000, 10.0, 0.6)
				runTest(10000, 10.0, 0.7)
				runTest(10000, 10.0, 0.8)
				runTest(10000, 10.0, 0.9)
			}
		}
	}
}
