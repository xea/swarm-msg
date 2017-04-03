package so.blacklight.swarm.smtp_old.policy

import so.blacklight.swarm.mail_old.Email

/**
	* Base trait for events that are used to control the policy evaluation flow
	*/
sealed trait PolicyEvent

/**
	* Base marker trait for indicating what effects a policy can have on a message
	*/
sealed trait PolicyEffect

trait EnvelopeEffect extends PolicyEffect
trait HeaderEffect extends PolicyEffect
trait DeliveryEffect extends PolicyEffect
trait PersistenceEffect extends PolicyEffect

/**
	* Base trait for representing possible outcomes of the policy evaluation process
	*/
sealed trait PolicyResult
sealed trait TerminalPolicyResult extends PolicyResult

final case class ProcessEmail(email: Email) extends PolicyEvent

final case class PolicyPass(email: Email) extends PolicyResult
final case class PolicyReject(reason: String) extends TerminalPolicyResult
