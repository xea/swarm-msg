package so.blacklight.swarm.smtp

trait SMTPClientEvent
trait SMTPServerEvent

/**
  */
// eg. 220 hostname.domainname.tld ESMTP Postfix (Ubuntu)
case class SMTPServerGreeting(greeting: String)
// eg. EHLO clientname
case class SMTPClientEhlo(hostId: String) extends SMTPClientEvent
// eg:
// 250-hostname.domainname.tld
// 250-PIPELINING
// 250-SIZE 10240000
// 250-VRFY
// 250-STARTTLS
// 250 8BITMIME
case class SMTPServerEhlo(capabilities: Array[String])

// eg. MAIL FROM: <user@company.com>
case class SMTPClientMailFrom(sender: String) extends SMTPClientEvent
case class SMTPClientReceiptTo(recipient: String) extends SMTPClientEvent
case object SMTPClientDataBegin extends SMTPClientEvent
case class SMTPClientDataEnd(data: String) extends SMTPClientEvent
case object SMTPClientReset extends SMTPClientEvent
case object SMTPClientQuit extends SMTPClientEvent
case object SMTPClientUnknownCommand extends SMTPClientEvent

case object SMTPServerOk extends SMTPServerEvent
case object SMTPServerDataOk extends SMTPServerEvent
case object SMTPServerQuit extends SMTPServerEvent
