package so.blacklight.swarm.mail.mime

import so.blacklight.swarm.mail.MediaType

/**
	*
	*/
class SingleMimePart(mediaType: MediaType) extends MimePart {
	override def getContentType: MediaType = mediaType
}
