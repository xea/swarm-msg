package so.blacklight.swarm.mail

class Email private (from: Address, to: Seq[Address], subject: String) {

}

object Email {
	def apply(envelope: Envelope, body: Array[Char]): Either[String, Email] = {
		Left("Error")
	}
}
