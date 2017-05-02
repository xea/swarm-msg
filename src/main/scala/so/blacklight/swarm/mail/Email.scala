package so.blacklight.swarm.mail


/**
	* Represents an e-mail message that is sent between two computers.
	*/
trait Message {

	def length: Int

}

/**
	* Represents an e-mail message as specified by the RFC2822 IETF standard.
	*
	* See also: https://tools.ietf.org/html/rfc2822
	*/
class RFC2822Message extends Message {

	def length: Int = 0

}

trait MimePart {

	def getContentType: MediaType

}

case class Header(key: String, value: String)

/**
	* A raw message represents the exact byte stream the message was built
	*
	* @param bytes original lines
	*/
class RawMessage(bytes: Array[Byte]) extends Message {

	def length: Int = bytes.length
}

class Utf8Message(lines: List[String]) extends Message {

	def length: Int = lines.foldLeft(0)(_ + _.length)

}
