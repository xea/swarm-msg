package so.blacklight.swarm.email.meh

/**
	*/
trait Domain {

}

object Domain {
	def apply(domainName: String): Option[Domain] = InternetDomain(domainName)
}

class InternetDomain private (domainName: String) extends Domain {

}

object InternetDomain {
	def apply(domainName: String): Option[InternetDomain] = {
		Some(new InternetDomain(domainName))
	}
}
