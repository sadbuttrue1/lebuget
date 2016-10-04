package tk.sadbuttrue.lebuget.microservice.auth.password


import slick.driver.PostgresDriver.api._
import slick.jdbc.JdbcBackend.Database

import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

/**
  * Created by true on 04/10/2016.
  */
case class EmailAddress(address: String) extends MappedTo[String] {
  override val value: String = address

  require(EmailAddress.isValid(address), "Invalid email address format")
}

object EmailAddress {
  def isValid(email: String): Boolean = EmailRegex.pattern.matcher(email.toUpperCase).matches()

  private val EmailRegex = """\b[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*\b""".r
}

case class AuthEntry(id: Option[Long], identityId: Long, createdAt: Long, email: EmailAddress, password: String)

class AuthEntries(tag: Tag) extends Table[AuthEntry](tag, "auth_entry") {
  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

  def identityId = column[Long]("identity_id")

  def createdAt = column[Long]("created_at")

  def email = column[EmailAddress]("email")

  def password = column[String]("password")

  override def * = (id, identityId, createdAt, email, password) <> (AuthEntry.tupled, AuthEntry.unapply)
}

class Repository extends Config {
  def createAuthEntry(entry: AuthEntry) = {
    val result = Await.ready(db.run(authEntries += entry), Duration.Inf).value.get
    result match {
      case Success(entry) => entry
      case Failure(e) => null
    }
  }

  def updateAuthEntry(entry: AuthEntry) = {
    val result = Await.ready(db.run(authEntries.filter(_.id === entry.id).update(entry)), Duration.Inf).value.get
    result match {
      case Success(entry) => entry
      case Failure(e) => null
    }
  }

  def findAuthEntry(email: EmailAddress): Option[AuthEntry] = {
    val result = Await.ready(db.run(byEmailCompiled(email).result.headOption), Duration.Inf).value.get
    result match {
      case Success(entry) => entry
      case Failure(e) => null
    }
  }

  private def byEmailQuery(email: Rep[EmailAddress]) = authEntries.filter(_.email === email)

  private val byEmailCompiled = Compiled(byEmailQuery _)

  private val authEntries = TableQuery[AuthEntries]

  private val db = Database.forURL(url = dbUrl, user = dbUser, password = dbPassword, driver = "org.postgresql.Driver")
}
