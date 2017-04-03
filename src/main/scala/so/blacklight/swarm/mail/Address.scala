package so.blacklight.swarm.mail

/**
	* An instance of Address is used to uniquely identify a particular user who is either the sender or a recipient
	* of an email transmission.
	*/
sealed trait Address {

	def getAddress: String

}

object Address {
	def apply(emailAddress: String): Address = {
		val nullAddr = "^$".r
		val emailExpr = "^([^@]+)@([^@]+)$".r

		emailAddress.trim.stripPrefix("<").stripSuffix(">") match {
			case nullAddr() => new NullAddress
			case emailExpr(user, domain) => EmailAddress(user, Domain(domain))
			case otherAddress => new IrregularAddress(otherAddress)
		}

	}
}

/**
	* Represents a null (<>) address that is used to indicate that a message is a notification and receiving systems
	* should not reply to a message from this sender.
	*/
class NullAddress extends Address {
	override def getAddress: String = "<>"
}

class RelayAddress private (domains: Seq[Domain], forwardPath: EmailAddress) extends Address {
	override def getAddress: String = s"<${domains.map(_.name).mkString(",")}:${forwardPath.getAddress}>"
}

class EmailAddress private (localPart: String, domain: Domain) extends Address {
	override def getAddress: String = s"<$localPart@${domain.name}>"
}

object EmailAddress {
	def apply(emailAddress: String): Option[EmailAddress] = {
		emailAddress.stripPrefix("<").stripSuffix(">").split("@") match {
			case Array(localPart, domain) =>
				Some(new EmailAddress(localPart, Domain(domain)))
		}
	}

	def apply(user: String, domain: Domain): EmailAddress = {
		new EmailAddress(user, domain)
	}
}

class IrregularAddress(address: String) extends Address {
	override def getAddress: String = address
}

/**
	* Represents an arbitrary domain name, eg. google.com
	*
	* @param domainName the fully qualified name of the domain
	*/
class Domain private (domainName: String) {
	def name: String = domainName
}

object Domain {
	def apply(domainName: String): Domain = {
		new Domain(domainName)
	}

}
