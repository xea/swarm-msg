package so.blacklight.swarm.email.meh

/**
	*
	*/
trait MimePart {

	def getHeaders: MimeHeaders

}

/*
class ContentBodyPart(headers: MimeHeaders, content: Content) extends MimePart {

	override def getHeaders = headers

}

trait Multipart extends MimePart {

}

class MultipartMixed[+P <: MimePart](headers: MimeHeaders, parts: Seq[P]) extends Multipart {

	def getParts: Seq[P] = {
		parts
	}

	override def getHeaders = headers
}

class MultipartAlternative[+P <: MimePart](headers: MimeHeaders, parts: Seq[P]) extends Multipart {

	def getParts: Seq[P] = {
		parts
	}

	override def getHeaders = headers
}

class MultipartMessage[+P <: Message](headers: MimeHeaders, message: P) extends Multipart {

	def getMessage: P = {
		message
	}

	override def getHeaders = headers
}

class MultipartSigned(headers: MimeHeaders) extends Multipart {

	override def getHeaders = headers

}

class MultipartEncrypted(headers: MimeHeaders) extends Multipart {

	override def getHeaders = headers

}
*/