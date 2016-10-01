package tk.sadbuttrue.lebuget.microservice.identity

import scala.concurrent._
import slick.driver.PostgresDriver.api._
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

/**
  * Created by true on 01/10/2016.
  */
case class Identity(id: Option[Long], createdAt: Long)

class Identities(tag: Tag) extends Table[Identity](tag, "identity") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def createdAt = column[Long]("created_at")

  override def * = (id.?, createdAt) <> ((Identity.apply _).tupled, Identity.unapply)
}

class Repository extends Config {
  def getAllIdentities: List[Identity] = {
    val result = Await.ready(db.run(identities.result), Duration.Inf).value.get
    result match {
      case Success(identities) => identities.toList
      case Failure(e) => List.empty
    }
  }

  def saveIdentity(identity: Identity): Identity = {
    val result = Await.ready(db.run(identities returning identities.map(_.id) into ((_, id) => identity.copy(id = Option(id))) += identity), Duration.Inf).value.get
    result match {
      case Success(identity) => identity
      case Failure(e) => null
    }
  }

  private val db = Database.forURL(url = dbUrl, user = dbUser, password = dbPassword, driver = "org.postgresql.Driver")
  private val identities = TableQuery[Identities]
}
