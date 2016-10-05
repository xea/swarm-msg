package so.blacklight.swarm.stream

import javax.net.ssl.SSLContext

import akka.NotUsed
import akka.actor.Actor
import akka.stream.ActorMaterializer
import akka.stream.TLSProtocol.{SslTlsInbound, SslTlsOutbound}
import akka.stream.scaladsl.Source

case class TriggerStream();

/**
	*
	*/
class StreamService extends Actor {

	val source: Source[Int, NotUsed] = Source(1 to 100)

	implicit val system = context.system
	implicit val materializer = ActorMaterializer()

	override def receive: Receive = {
		case TriggerStream => {
			source.runForeach(i => println(i))
		}
		case _ => ()
	}
}

// ---------

import scala.util._

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import akka.util._

object SimpleTcpServer extends App {

	val address = "127.0.0.1"
	val port = 6666

	val serverLogic = Flow[ByteString]
		.via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
		.map(_.utf8String)
		.map(msg => s"Server hereby responds to message: $msg\n")
		.map(ByteString(_))

	def mkServer(address: String, port: Int)(implicit system: ActorSystem, materializer: Materializer): Unit = {
		import system.dispatcher

		val connectionHandler = Sink.foreach[Tcp.IncomingConnection] { conn =>
			println(s"Incoming connection from: ${conn.remoteAddress}")
			conn.handleWith(serverLogic)
		}

		val ctx = SSLContext.getInstance("TLSv1.2")

		val flow: BidiFlow[SslTlsOutbound, ByteString, ByteString, SslTlsInbound, NotUsed] = TLS(ctx, TLSProtocol.NegotiateNewSession, TLSRole.server)

		val incomingCnnections = Tcp().bind(address, port)
		val binding = incomingCnnections.to(connectionHandler).run()
		//val binding = incomingCnnections.via(TLS(ctx, TLSProtocol.NegotiateNewSession, TLSRole.server)).to(connectionHandler).run()

		binding onComplete {
			case Success(b) =>
				println(s"Server started, listening on: ${b.localAddress}")
			case Failure(e) =>
				println(s"Server could not be bound to $address:$port: ${e.getMessage}")
		}
	}

	def mkAkkaServer() = {
		implicit val server = ActorSystem("Server")
		implicit val materializer = ActorMaterializer()
		mkServer(address, port)
	}

	mkAkkaServer()
}