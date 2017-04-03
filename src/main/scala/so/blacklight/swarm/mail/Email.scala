package so.blacklight.swarm.mail

/**
	* Created by xea on 3/25/2017.
	*/
class Email {
	val headers: Seq[MessageHeader] = List()
}

object Email {
	def apply(content: Array[Char]): Option[Email] = {
		Email(Stream(content: _*))
	}

	def apply(content: Stream[Char]): Option[Email] = {
		None
	}
}

trait MimePart {

}