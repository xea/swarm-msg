package so.blacklight.swarm.mail

/**
	* Represents an arbitrary valid e-mail address
	*/
class Address private (address: String) {

	def isPostmaster(): Boolean = "<>".equals(address)
}

object Address {
	def empty(): Address = {
		new Address("<>")
	}

	def apply(address: String): Either[String, Address] = {
		// TODO implement e-mail address validation
		if (address == null || address.trim.length == 0) {
			Left("Null or empty address")
		} else {
			Right(new Address(address.trim))
		}
	}
}

