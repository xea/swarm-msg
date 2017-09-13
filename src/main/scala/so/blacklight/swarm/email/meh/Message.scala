package so.blacklight.swarm.email.meh

import so.blacklight.swarm.email.Envelope

/**
	*/
trait Message {

}

abstract class Email extends Message {

	def getSubject(): Option[String]

}

class RFC2822Email(message: MimePart) extends Email {

	override def getSubject() = None

	def getMessage(): MimePart = {
		message
	}

}