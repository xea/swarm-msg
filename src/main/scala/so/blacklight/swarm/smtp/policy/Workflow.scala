package so.blacklight.swarm.smtp.policy


final case class Sequential(steps: PolicyEffect*)

final case class FireAndForget(steps: PolicyEffect*)

