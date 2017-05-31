package so.blacklight.swarm.mail_old


/**
	* Represents an e-mail message that is sent between two computers.
	*/
trait Message {

	/**
		* Return the total length of the message (excluding envelope but including headers and subparts)
		* expressed in bytes
		*
		* @return message length in bytes
		*/
	def length: Int

}

/**
	* Represents an e-mail message as specified by the RFC2822 (and RFC5322) IETF standard.
	*
	* See also: https://tools.ietf.org/html/rfc2822
	* See also: https://tools.ietf.org/html/rfc5322
	*/
class RFC2822Message extends Message {

	def length: Int = 0

}

/**
	* A header represents an arbitrary metadata describing a Message instance in the
	* form of a key-value pair.
	*
	* @tparam K type of the header key (typically String)
	* @tparam V type of the header value (typically String)
	*/
trait Header[K, V] {

	def getKey: K

	def getValue: K

}

class MimeHeader(key: String, value: String) extends Header[String, String] {

	override def getKey: String = key

	override def getValue: String = value

}

/**
	* A raw message represents the exact byte stream the message was built
	*
	* @param bytes original lines
	*/
/*
class RawMessage(bytes: Array[Byte]) extends Message {

	def length: Int = bytes.length

	def getBytes: Array[Byte] = bytes


class SlicedRawMessage(bytes: Array[Byte]) extends RawMessage(bytes) {

}

object SlicedRawMessage {
	def apply(rawMessage: RawMessage): SlicedRawMessage = new SlicedRawMessage(rawMessage.getBytes)
}
*/

/*
class Utf8Message(lines: List[String]) extends Message {

	def length: Int = lines.foldLeft(0)(_ + _.length)

}
*/

