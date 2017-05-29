package so.blacklight.swarm.mail_old.mime

import so.blacklight.swarm.mail_old.MediaType

/**
	*
	*/
class MixedMultipart extends MimeMultipart {

	override def getContentType: MediaType = MediaType.Mixed

	def getParts(): Seq[MimePart] = {
		List()
	}
}
