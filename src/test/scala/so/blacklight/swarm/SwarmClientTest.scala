package so.blacklight.swarm

import java.net.Socket

import akka.actor.ActorSystem
import so.blacklight.swarm.mail.{Email, Envelope}
import so.blacklight.swarm.smtp.SMTPConnector

/**
	*
	*/
class SwarmClientTest {

}

object SwarmClientTest extends App {

	val system = ActorSystem("SwarmClient")

	Envelope("test@address.com", List("test@address.com")).map(envelope => {
		val body = "Subject: Blabalab\r\n\r\nTest body".toCharArray
		Email(envelope, body) match {
			case Right(email) =>
				val socket = new Socket("localhost", 5025)
				val connector = system.actorOf(SMTPConnector.props())

				//connector ! DeliverMessages(socket, Stream(email, email, email))
				//connector ! DeliverMessages(socket, Stream.continually(() => email).map(_()))
			case _ => ()
		}
	})

	println("asdfasdf")

}
