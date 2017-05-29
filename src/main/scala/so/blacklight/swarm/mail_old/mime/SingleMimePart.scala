package so.blacklight.swarm.mail_old.mime

import so.blacklight.swarm.mail_old.MediaType

/**
	*
	*/
class SingleMimePart(mediaType: MediaType) extends MimePart {
	override def getContentType: MediaType = mediaType
}
