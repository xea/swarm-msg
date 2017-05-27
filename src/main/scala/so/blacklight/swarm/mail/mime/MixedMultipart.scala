package so.blacklight.swarm.mail.mime

import so.blacklight.swarm.mail.MediaType

/**
	*
	*/
class MixedMultipart extends MimeMultipart {

	override def getContentType: MediaType = MediaType.Mixed

	def getParts(): Seq[MimePart] = {
		List()
	}
}
