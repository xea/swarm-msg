package so.blacklight.swarm

import java.net.Socket

import akka.actor.ActorSystem
import so.blacklight.swarm.mail.{Email, Envelope}
import so.blacklight.swarm.smtp.{DeliverMessage, SMTPConnector}

/**
	*
	*/
class SwarmClientTest {

}

object SwarmClientTest extends App {

	val system = ActorSystem("SwarmClient")

	Envelope("sender@address.com", List("recipient@address.com")).map(envelope => {
		val body = "Test body".toCharArray
		Email(envelope, body) match {
			case Right(email) =>
				val socket = new Socket("localhost", 5025)
				system.actorOf(SMTPConnector.props()) ! DeliverMessage(socket, email)
			case _ => ()
		}
	})

	println("asdfasdf")

}
