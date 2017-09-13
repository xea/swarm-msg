package so.blacklight.swarm.email.meh

/**
	* Represents a single user.
	*/
class User private (username: String) {

}

object User {
	def apply(username: String): Option[User] = {
		Some(new User(username))
	}
}
