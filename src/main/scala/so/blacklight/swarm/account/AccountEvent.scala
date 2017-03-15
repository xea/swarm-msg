package so.blacklight.swarm.account


sealed trait AccountRequest
sealed trait AccountResponse

// Account-related requests
final case class LookupAccount(query: (Account) => Boolean) extends AccountRequest

// Account-related responses
final case object NoSuchAccount extends AccountResponse
final case class AccountInfo(account: Account) extends AccountResponse
