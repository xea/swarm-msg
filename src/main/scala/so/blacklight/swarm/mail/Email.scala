package so.blacklight.swarm.mail

/**
	*
	*/
class Address private (address: String) {

	def isPostmaster(): Boolean = "<>".equals(address)
}

object Address {

	def apply(address: String): Either[Address, String] = {
		// TODO email address validation comes here
		if (address == null || address.trim.length == 0) {
			Right("Null or empty address")
		} else {
			Left(new Address(address.trim))
		}
	}
}

class Email(from: Address, to: Seq[Address], subject: String) {

}
