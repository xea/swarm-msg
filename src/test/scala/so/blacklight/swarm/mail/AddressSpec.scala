package so.blacklight.swarm.mail

import org.scalatest.{FlatSpec, FunSpec}

class AddressSpec extends FlatSpec {

	"A null address" should "be resolved as an instance of NullAddress" in {
		assert(Address("<>").isInstanceOf[NullAddress])
	}

	"A regular address" should "be resolve as an instance of EmailAddress" in {
		assert(Address("user@domain.com").isInstanceOf[EmailAddress])
		assert(Address("<user@domain.com>").isInstanceOf[EmailAddress])
	}

	"A relay address" should "be resolved as an instance of RelayAddress" in {
		assert(Address("<@a,@b,@c:user@domain.com>").isInstanceOf[RelayAddress])
	}

	"An invalid address" should "Be resolved as an instance of InvalidAddress" in {
		assert(Address("@test@$").isInstanceOf[IrregularAddress])
	}

	"Brackets (<>)" should "be added when querying address" in {
		assert(Address("<user@domain.com>").getAddress == "<user@domain.com>")
	}
}

class DomainSpec extends FunSpec {
	describe("apply()") {
		it("should accept any valid domain names") {
			assert(Domain("domain.com").isInstanceOf[Domain])

			/*
			assertThrows[NoSuchElementException] {
				()
			}
			*/
		}

		it ("should accept international domain names") {
			fail("Not implemented")
		}
	}
}
