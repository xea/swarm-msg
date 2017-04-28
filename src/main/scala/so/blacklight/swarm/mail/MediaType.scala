package so.blacklight.swarm.mail

/**
	* Top-level media type is used to specify the general type of the data
	*
	* @param topMediaType the name of the top-level media type
	*/
sealed case class TopMediaType(topMediaType: String)

object TopMediaType {
	object Text extends TopMediaType("text")
	object Image extends TopMediaType("image")
	object Audio extends TopMediaType("audio")
	object Video extends TopMediaType("video")
	object Application extends TopMediaType("application")
	object Message extends TopMediaType("message")
	object Multipart extends TopMediaType("multipart")
	class NonStandard(mediaType: String) extends TopMediaType(mediaType)
}

/**
	* Media type is used to specify the exact format of some content
	*
	* @param topType top-level media type this type belongs to
	* @param discreteType the name of this specific type
	*/
sealed case class MediaType(topType: TopMediaType, discreteType: String)

object MediaType {
	import TopMediaType._

	object PlainText extends MediaType(Text, "plain")
	object HTML extends MediaType(Text, "html")
	object RFC822Message extends MediaType(Message, "rfc822")
	object JPEG extends MediaType(Image, "jpeg")
	object OctetStream extends MediaType(Application, "octet-stream")
	object PostScript extends MediaType(Application, "postscript")
	object XML extends MediaType(Application, "xml")
	object Mixed extends MediaType(Multipart, "mixed")
	object Alternative extends MediaType(Multipart, "alternative")
	class Other(topMediaType: TopMediaType, mediaType: String) extends MediaType(topMediaType, mediaType)
}


