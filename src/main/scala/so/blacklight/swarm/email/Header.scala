package so.blacklight.swarm.email

/**
	*
	*/

trait Header[+K, +V] {

	def getKey: K

	def getValue: V

}

class EmailHeader(key: String, value: String) extends Header[String, String] {

	override def getKey: String = key

	override def getValue: String = value

}

class EmailHeaders(headers: Seq[EmailHeader]) {

	def getHeader(name: String): Option[EmailHeader] = headers.find(_.getKey == name)

	def getHeaderValue(name: String): Option[String] = getHeader(name).map(_.getValue)

}
