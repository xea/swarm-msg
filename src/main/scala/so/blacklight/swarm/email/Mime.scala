package so.blacklight.swarm.email

trait MimeBodyPart {

	def getHeaders: EmailHeaders

}

class MimeMessage(headers: EmailHeaders, body: MimeBodyPart) extends MimeBodyPart {

	override def getHeaders = headers
}

abstract class MimeMultipart[+B <: MimeBodyPart](headers: EmailHeaders, parts: Seq[B]) extends MimeBodyPart {

	override def getHeaders: EmailHeaders = headers

	def getBodyParts: Seq[B] = parts
}

class MultipartMixed(headers: EmailHeaders, parts: Seq[MimeBodyPart]) extends MimeMultipart(headers, parts) {


}

class MultipartDigest(headers: EmailHeaders, parts: MimeMessage) extends MimeMultipart(headers, List(parts)) {

}
