package so.blacklight.swarm.email.meh

/**
	*
	*/
trait Content {

}

abstract class TextContent extends Content {

}

/**
	* Represents a plain text content, that is unencrypted
	*/
abstract class PlainTextContent extends TextContent {

}

abstract class ImageContent extends Content {

}