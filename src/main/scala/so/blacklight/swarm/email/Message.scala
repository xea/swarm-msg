package so.blacklight.swarm.email

trait Message[+T] {
	def getContent: Stream[T]
}

trait MimePart {
	def getContentType: String
}

trait MimeMessage {

	def getHeaders: Seq[MimeHeader]

	def getSubject: Option[String]

	def getFrom: Option[String]

	def getTo: Option[String]

}

trait Header[+K, +V] {

	def getHeader: K

	def getValue: V

}

class MimeHeader(header: String, value: String) extends Header[String, String] {

	override def getHeader: String = header

	override def getValue: String = value

}

class RFC2822Message(headers: Seq[MimeHeader]) extends MimeMessage {

	protected def getHeader(header: String): Option[MimeHeader] = getHeaders find { _.getHeader.toLowerCase == header.toLowerCase }

	protected def getHeaderValue(header: String): Option[String] = getHeader(header) map { _.getValue }

	override def getHeaders: Seq[MimeHeader] = headers

	override def getSubject: Option[String] = getHeaderValue("Subject")

	override def getFrom: Option[String] = getHeaderValue("From")

	override def getTo: Option[String] = getHeaderValue("To")

}

