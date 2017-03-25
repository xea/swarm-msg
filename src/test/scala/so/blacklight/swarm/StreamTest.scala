package so.blacklight.swarm

import so.blacklight.swarm.mail.EmailAddress

/**
	*
	*/
class StreamTest {


}

object StreamTest extends App {

	val address = EmailAddress("valami@domain.hu")

	println(address)

	/*
	Stream(1) match {
		case a #:: b #:: c => println("Three")
		case a #:: b => println("Two")
		case a => println("One")
	}
	*/

	/*
	var i = 0
	println(Stream.continually(() => { i = i + 2; i }).map(f => f()).take(3).toList)

	i = 0

	println(Stream.continually(() => { i = i + 1; i }).map(f => f()).takeWhile(_ <= 4).toList)
	println(i)
	*/

}
