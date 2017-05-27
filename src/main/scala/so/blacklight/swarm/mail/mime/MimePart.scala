package so.blacklight.swarm.mail.mime

import so.blacklight.swarm.mail.MediaType

/**
	*
	*/
trait MimePart {

	def getContentType: MediaType

}
