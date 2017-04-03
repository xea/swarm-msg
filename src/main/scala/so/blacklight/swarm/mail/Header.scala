package so.blacklight.swarm.mail

trait MessageHeader {
	def getKey: String
	def getValue: String
}

sealed trait RawPart {
	def getBytes(): Array[Byte]
}

class PlainMessageHeader(key: String, value: String) extends MessageHeader {

	override def getKey: String = key

	override def getValue: String = value
}

object PlainMessageHeader {
	def apply(key: String, value: String): PlainMessageHeader = {
		new PlainMessageHeader(key, value)
	}
}

class RawMessageHeader private (bytes: Array[Char]) extends MessageHeader with RawPart {

	override def getKey: String = ???

	override def getValue: String = ???

	override def getBytes(): Array[Byte] = ???
}


object RawMessageHeader {
	def apply(headerLine: Array[Char]): Option[RawMessageHeader] = {
		None
	}
}
