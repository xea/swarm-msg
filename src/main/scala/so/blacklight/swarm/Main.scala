package so.blacklight.swarm

/**
  * The executable entry point of the application.
  */
object Main extends App {

  val serverInstance = new SwarmServer

  serverInstance.start
}

