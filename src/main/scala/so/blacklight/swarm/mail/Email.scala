package so.blacklight.swarm.mail

class Email private (envelope: Envelope, subject: String) {

}

object Email {
	def apply(envelope: Envelope, body: Array[Char]): Either[String, Email] = {
		Right(new Email(envelope, new String(body)))
	}
}
