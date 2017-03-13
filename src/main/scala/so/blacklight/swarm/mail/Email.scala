package so.blacklight.swarm.mail

class Email private (envelope: Envelope, subject: String) {

	def getEnvelope(): Envelope = envelope

	def getBody(): Array[Char] = Array()
}

object Email {
	def apply(envelope: Envelope, body: Array[Char]): Either[String, Email] = {
		Right(new Email(envelope, new String(body)))
	}
}
