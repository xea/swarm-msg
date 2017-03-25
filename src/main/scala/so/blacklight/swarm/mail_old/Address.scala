package so.blacklight.swarm.mail_old

/**
	* Represents an arbitrary string that can be used as an e-mail address.
	*/
class Address private (address: String) {

	/**
		* Indicates whether this e-mail address is a null (<>) address
		* @return true if the address is a null address
		*/
	def isNullAddress(): Boolean = Address.NULL.equals(address)

	def toEmailAddress(): String = s"$address"

	override def toString: String = s"Address($address)"
}

object Address {

	def NULL: String = "<>"

	/**
		* Create a new address instance representing a null (<>) address
		* @return null address instance
		*/
	def nullAddress(): Address = {
		new Address(NULL)
	}

	/**
		* Tries to create a new address instance using the input parameter and returns an Either describing the result.
		*
		* If the input parameter was a valid e-mail address, then the return value will be a Right(Address), otherwise a
		* Left(String) where the contained String is an error message explaining the problem.
		*
		* @param address input address
		* @return parse result
		*/
	def apply(address: String): Either[String, Address] = {
		// TODO implement e-mail address validation
		if (address == null || address.trim.length == 0) {
			Left("Null or empty address")
		} else {
			Right(new Address(address.trim))
		}
	}
}

