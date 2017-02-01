package so.blacklight.swarm.storage

/**
	* Identifies a stored object uniquely. Instances of StorageId are used to access object stored in ObjectStorage
	*
	* @param id storage ID referring to a stored object
	*/
class StorageId(id: String) {
	def storageId(): String = {
		id
	}
}
