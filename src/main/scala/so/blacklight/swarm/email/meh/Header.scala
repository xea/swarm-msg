package so.blacklight.swarm.email.meh

/**
	* Headers can describe metadata about message contents, like
	* version names, message types, etc.
	*/
trait Header[+H, +V] {

	def getName: H

	def getValue: V

}

/**
	* An RFC2822 (actually, RFC5322) header consists of printable US-ASCII characters
	* (except colon ':') only. RFC2822 header values are composed of printable US-ASCII
	* characters and space and tab characters.
	*/
trait RFC2822Header extends Header[String, String] {

}

class Headers[H, +V](headers: Seq[Header[H, V]]) {

	def getHeader(name: H): Option[Header[H, V]] = {
		None
	}

	def getHeaderValue(name: H): Option[V] = {
		getHeader(name).map(_ getValue)
	}
}

class MimeHeaders(headers: Seq[Header[String, String]]) extends Headers[String, String](headers)

