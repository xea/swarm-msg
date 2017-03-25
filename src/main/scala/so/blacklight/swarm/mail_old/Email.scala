package so.blacklight.swarm.mail_old

class Email private (envelope: Envelope, body: String) {

	def getEnvelope(): Envelope = envelope

	def getBody(): Array[Char] = body.toCharArray

	def isLoopback(): Boolean = true
}

object Email {
	def apply(envelope: Envelope, body: Array[Char]): Either[String, Email] = {
		Right(new Email(envelope, new String(body)))
	}
}
