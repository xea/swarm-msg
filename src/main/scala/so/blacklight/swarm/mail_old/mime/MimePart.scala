package so.blacklight.swarm.mail_old.mime

import so.blacklight.swarm.mail_old.MediaType

/**
	*
	*/
trait MimePart {

	def getContentType: MediaType

}
