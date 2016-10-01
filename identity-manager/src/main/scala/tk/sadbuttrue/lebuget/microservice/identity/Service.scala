package tk.sadbuttrue.lebuget.microservice.identity

/**
  * Created by true on 01/10/2016.
  */
class Service(repository: Repository) {
  def crateIdentity: Identity = {
    val newIdentity = Identity(id = None, createdAt = System.currentTimeMillis())
    repository.saveIdentity(newIdentity)
  }

  def listAllIdentities: List[Identity] = {
    repository.getAllIdentities
  }
}
