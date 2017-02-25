package so.blacklight.swarm.mail

trait Address {
	def isPostmaster(): Boolean
}

/**
	* Represents an arbitrary valid e-mail address
	*/
class EmailAddress private (address: String) extends Address {

	def isPostmaster(): Boolean = "<>".equals(address)
}

class RawAddress(address: String) extends Address {
	override def isPostmaster(): Boolean = false
}

object Address {
	def empty(): RawAddress = {
		new RawAddress("<>")
	}

	def apply(address: String): Either[String, Address] = {
		// TODO implement e-mail address validation
		if (address == null || address.trim.length == 0) {
			Left("Null or empty address")
		} else {
			Right(new EmailAddress(address.trim))
		}
	}
}


