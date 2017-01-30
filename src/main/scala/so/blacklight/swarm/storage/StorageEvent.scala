package so.blacklight.swarm.storage

/**
	*
	*/
class StorageEvent()
case class StoreRequest(target: Any)
case class RetrieveRequest(id: StorageId)
case class StorageOK(id: StorageId) extends StorageEvent
case class LookupResponse(target: Any)
