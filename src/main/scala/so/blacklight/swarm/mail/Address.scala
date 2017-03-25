package so.blacklight.swarm.mail

/**
	*
	*/
trait Address {

}

class NullAddress extends Address {

}

class EmailAddress private (localPart: String, domain: Domain) extends Address {

}

object EmailAddress {
	def apply(emailAddress: String): Option[EmailAddress] = {
		emailAddress.split("@") match {
			case Array(localPart, domain) =>
				Some(new EmailAddress(localPart, Domain(domain)))
		}
	}
}

class Domain private (domainName: String)

object Domain {
	def apply(domainName: String): Domain = {
		(new Domain(domainName))
	}

}
