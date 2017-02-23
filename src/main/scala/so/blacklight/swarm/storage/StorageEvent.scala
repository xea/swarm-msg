package so.blacklight.swarm.storage

/**
	*
	*/
class StorageEvent()
case class StoreRequest(target: Any)
case class RetrieveRequest(id: StorageId)
case class ListEntities(filter: Option[String])
case class EntityList(entities: List[(StorageId, Class[_])])
case class StorageOK(id: StorageId) extends StorageEvent
case class LookupResponse(target: Any)
