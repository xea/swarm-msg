package so.blacklight.swarm.mail


/**
	* Represents an e-mail message that is sent between two computers.
	*/
trait Message {

}

/**
	* Represents an e-mail message as specified by the RFC2822 IETF standard.
	*
	* See also: https://tools.ietf.org/html/rfc2822
	*/
class RFC2822Message extends Message {

}

trait MimePart {

	def getContentType: MediaType

}

case class Header(key: String, value: String)

/**
	* A raw message represents the exact byte stream the message was built
	*
	* @param lines original lines
	*/
class RawMessage(lines: List[String]) extends Message {

	def length: Int = lines.foldLeft(0)(_ + _.length)

}
