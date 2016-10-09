package so.blacklight.swarm.control

/**
  * Control message to instruct Swarm actors to start their primary operations. It is usually
  * sent when all the necessary initialisation steps have completed.
  */
case object StartService

/**
  * Control message to instruct Swarm actors to shut down their primary services.
  */
case object StopService

