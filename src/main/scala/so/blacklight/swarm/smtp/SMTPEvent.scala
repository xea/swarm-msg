package so.blacklight.swarm.smtp

trait SMTPClientEvent;

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

case object SMTPClientQuit extends SMTPClientEvent
case object SMTPClientUnknownCommand extends SMTPClientEvent
