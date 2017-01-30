package so.blacklight.swarm.storage

import java.util.UUID

import akka.actor.Actor
import akka.event.Logging
import so.blacklight.swarm.control.{StartService, StopService}

import scala.collection.mutable

/**
	*
	*/
class ObjectStorage extends Actor {

	private val memoryStorage = new mutable.HashMap[String, Any]()

	private val logger = Logging(context.system, this)

	override def receive: Receive = {
		case StartService => startService()
		case StopService => stopService()
		case StoreRequest(target) => sender() ! store(target)
		case RetrieveRequest(request) => sender() ! retrieve(request)
	}

	private def startService(): Unit = {
		logger.info("Object Storage starting up")
		logger.info("Object Storage started")
	}

	private def stopService(): Unit = {
		memoryStorage.clear()
		logger.info("Object Storage stopped")
	}

	private def store(target: Any): StorageEvent = {
		val newKey = UUID.randomUUID.toString
		memoryStorage.put(newKey, target)
		StorageOK(new StorageId(newKey))
	}

	private def retrieve(target: StorageId): Any = {
		LookupResponse(Option(memoryStorage.get(target.storageId())))
	}

}
