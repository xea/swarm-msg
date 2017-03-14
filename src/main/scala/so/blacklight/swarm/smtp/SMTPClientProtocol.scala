package so.blacklight.swarm.smtp

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import so.blacklight.swarm.mail.{Address, Email}

/**
	*
	*/
class SMTPClientProtocol(clientSession: ActorRef, connector: ActorRef, msgStream: Stream[Email]) extends Actor {

	import context._

	val logger = Logging(system, this)

	override def receive: Receive = {
		case InitTransaction =>
			clientSession ! InitTransaction
			logger.info("Initiating transaction")
			become(expectGreeting)

		case _ => become(expectGreeting)
	}

	def expectGreeting: PartialFunction[Any, Unit] = {
		case SMTPServerServiceReady(_) =>
			sender() ! SMTPClientEhlo("localhost")
			logger.info("Got server reply, sent ehlo, waiting reply to ehlo")
			become(expectEhlo)

		case SMTPServerServiceNotAvailable =>
			sender() ! SMTPClientQuit

		case unknownMessage =>
			logger.warning(s"Expected server greeting but unknown message was received: $unknownMessage")
	}

	def expectEhlo: PartialFunction[Any, Unit] = {
		case SMTPServerEhlo(features) =>
		case SMTPServerOk =>
			logger.info(s"Everything looks good, stream has ${msgStream.length} items")
			become(loopMessages(msgStream))
			self ! NextMessage

			/*
			features.filter(feature => "STARTTLS".equals(feature.toUpperCase))
				.headOption
			  .map(_ => sender() ! SMTPClientStartTLS)
			  .getOrElse(() =>
					become(deliverMessage(msgStream.head))
					sender() ! )
					*/
		case other =>
			logger.warning(s"Unknown message received: $other")
	}

	def loopMessages(messageStream: Stream[Email]): PartialFunction[Any, Unit] = {
		case NextMessage =>
			logger.info(s"Taking next message, there are ${messageStream.length} items left")

			messageStream match {
				case x #:: _ =>
					become(preTransmission(x), false)
					self ! BeginTransmission
				case _ =>
					unbecome()
					logger.info("No more messages :'(")
			}

		case MessageSent =>
			messageStream match {
				case _ #:: remainder =>
					become(loopMessages(remainder))
				case _ => ()
			}
			self ! NextMessage
	}

	def preTransmission(message: Email): PartialFunction[Any, Unit] = {
		case BeginTransmission =>
			clientSession ! SMTPClientMailFrom(message.getEnvelope().getSender().toEmailAddress())

		// 250
		case SMTPServerOk =>
			logger.info("Sender accepted")
			become(listRecipients(message.getEnvelope().getRecipients()), false)
			self ! NextRecipient

		// 451
		case SMTPServerLocalError =>
			logger.error("Sender rejected :(")
			become(closeConnection)
			self ! SMTPClientQuit

		case RecipientsSent =>
			become(sendData(message.getBody()), true)
			self ! RecipientsSent

		case _ => ()
	}

	def listRecipients(recipients: List[Address]): PartialFunction[Any, Unit] = {
		case NextRecipient =>
			recipients.headOption.map(recipient => {
				clientSession ! SMTPClientReceiptTo(recipient.toEmailAddress())
			})
		case SMTPServerOk =>
			recipients.tail match {
				case List() =>
					//become(sendData, false)
					unbecome()
					self ! RecipientsSent
				case remaining =>
					become(listRecipients(remaining), true)
					self ! NextRecipient
			}
		case SMTPServerLocalError =>
			logger.error("Recipient rejected")
			unbecome()
		case _ => ()
	}

	def sendData(body: Array[Char]): PartialFunction[Any, Unit] = {
		case RecipientsSent =>
			clientSession ! SMTPClientDataBegin
		case SMTPServerDataReady =>
			clientSession ! SMTPClientDataEnd(body)
		case SMTPServerOk =>
			logger.info("Message accepted")
			unbecome()
			self ! MessageSent
	}

	def closeConnection: PartialFunction[Any, Unit] = {
		case SMTPClientQuit =>
			clientSession ! SMTPClientQuit
		case SMTPServerQuit =>
			clientSession ! ClientDisconnected
		case _ =>
			logger.warning("Unknown message received while terminating connection")
	}
}

object SMTPClientProtocol {

	def props(session: ActorRef, connector: ActorRef, msgStream: Stream[Email]): Props = {
		Props(new SMTPClientProtocol(session, connector, msgStream))
	}
}

case object BeginTransmission
case object NextRecipient
case object RecipientsSent
case object MessageSent
case object NextMessage