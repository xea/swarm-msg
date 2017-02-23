package so.blacklight.swarm.storage

import java.util.UUID

import akka.actor.Actor
import akka.event.Logging
import so.blacklight.swarm.control.{StartService, StopService}

import scala.collection.mutable

/**
	* ObjectStorage provides a generic way of storing and retrieving objects in an abstract way so that clients don't
	* have to know about storage or implementation details
	*
	* The current, rather trivial implementation does not actually persist the stored objects, they are kept in memory
	* instead. This solution, however fast it may be, is suboptimal and shall be replaced with one that is capable of
	* actual persistence
	*/
class ObjectStorage extends Actor {

	// Objects are kept in a memory backed mutable hash map.
	private val memoryStorage = new mutable.HashMap[StorageId, Any]()

	private val logger = Logging(context.system, this)

	override def receive: Receive = {
		case StartService => startService()
		case StopService => stopService()
		case StoreRequest(target) => sender() ! store(target)
		case RetrieveRequest(request) => sender() ! retrieve(request)
		case ListEntities(_) => sender() ! listEntities()
		case unknownMessage => logger.warning(s"Received unknown message: $unknownMessage")
	}

	private def startService(): Unit = {
		logger.info("Object Storage starting up")
		memoryStorage.clear()
		logger.info("Object Storage started")
	}

	private def stopService(): Unit = {
		logger.info("Object Storage stopping")
		memoryStorage.clear()
		logger.info("Object Storage stopped")
	}

	private def store(target: Any): StorageEvent = {
		val newKey = new StorageId(UUID.randomUUID.toString)
		memoryStorage.put(newKey, target)
		StorageOK(newKey)
	}

	private def retrieve(target: StorageId): Any = {
		LookupResponse(Option(memoryStorage.get(target)))
	}

	private def listEntities(): EntityList = {
		EntityList(memoryStorage.map(pair => (pair._1, pair._2.getClass)).toList)
	}
}
