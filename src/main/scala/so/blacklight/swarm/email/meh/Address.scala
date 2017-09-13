package so.blacklight.swarm.email.meh

/**
	* An e-mail address is used to uniquely identify a sender or a recipient of an e-mail conversation
	*/
trait Address {
}

object Address {

	/**
		* Attempt to create an Address object using the supplied String. It will try and
		* convert the string to it's possible most precise validated representation or failing
		* that, an InvalidAddress explaining why the address was invalid
		*
		* @param rawAddress string representation of the address
		* @return either the validated address object or an error explanation
		*/
	def apply(rawAddress: String): Either[InvalidAddress, Address] = {
		Left(InvalidAddress(s"Parsing raw addresses ($rawAddress) is not implemented"))
	}

	def apply(local: User, domain: Domain): EmailAddress = {
		EmailAddress(local, domain)
	}

	def apply(maybeLocal: Option[User], maybeDomain: Option[Domain]): Either[InvalidAddress, EmailAddress] = {
		(maybeLocal, maybeDomain) match {
			case (Some(user), Some(domain)) => Right(EmailAddress(user, domain))
			case (None, _) => Left(InvalidAddress("Invalid local user"))
			case (_, None) => Left(InvalidAddress("Invalid domain"))
		}
	}
}

/**
	* A NullAddress is a technical address used to mark messages as originating
	* from a mail delivery system, rather than an actual user.
	*/
class NullAddress extends Address

/**
	* An EmailAddress is a party in an e-mail transmission, either a sender or a
	* recipient. An e-mail address may identify a single user or a group of users.
	*
	* @param local the local name of the user
	* @param domain the domain name of the address
	*/
class EmailAddress(local: User, domain: Domain) extends Address {

}

object EmailAddress {
	def apply(user: User, domain: Domain): EmailAddress = new EmailAddress(user, domain)
}

class RawAddress(address: String) extends Address {
}

object RawAddress {
	def apply(address: String): RawAddress = new RawAddress(address)
}

case class InvalidAddress(reason: String)