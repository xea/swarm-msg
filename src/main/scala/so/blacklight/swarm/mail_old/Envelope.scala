package so.blacklight.swarm.mail_old

class Envelope(from: Address, to: List[Address]) {

	def getSender(): Address = from

	def getRecipients(): List[Address] = to
}

object Envelope {
	def apply(from: Address, to: List[Address]): Envelope = {
		new Envelope(from, to)
	}

	def apply(from: String, to: List[String]): Either[String, Envelope] = {
		Address(from) match {
			case Left(error) => Left(error)
			case Right(address) =>
				val maybeRecipients: List[Either[String, Address]] = to.map(Address(_))

				if (maybeRecipients.exists(_.isLeft)) {
					Left(maybeRecipients
						.filter(_.isLeft)
						.map(_.swap)
						.map(_.getOrElse("Unknown error"))
					  .head)
				} else {
					Right(Envelope(address, maybeRecipients
					  .map(_.getOrElse(Address.nullAddress()))))
				}
		}
	}

}

