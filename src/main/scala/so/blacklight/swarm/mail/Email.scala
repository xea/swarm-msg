package so.blacklight.swarm.mail

class Email private (envelope: Envelope, body: String) {

	def getEnvelope(): Envelope = envelope

	def getBody(): Array[Char] = body.toCharArray
}

object Email {
	def apply(envelope: Envelope, body: Array[Char]): Either[String, Email] = {
		Right(new Email(envelope, new String(body)))
	}
}
