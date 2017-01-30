package so.blacklight.swarm

/**
	*
	*/
class StreamTest {


}

object StreamTest extends App {

	var i = 0
	println(Stream.continually(() => { i = i + 2; i }).map(f => f()).take(3).toList)
}
