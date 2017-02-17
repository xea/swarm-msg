package so.blacklight.swarm.mail

/**
	* Represents an arbitrary valid e-mail address
	*/
class Address private (address: String) {

	def isPostmaster(): Boolean = "<>".equals(address)
}

object Address {

	def apply(address: String): Either[Address, String] = {
		// TODO implement e-mail address validation
		if (address == null || address.trim.length == 0) {
			Right("Null or empty address")
		} else {
			Left(new Address(address.trim))
		}
	}
}

class Email(from: Address, to: Seq[Address], subject: String) {

}
