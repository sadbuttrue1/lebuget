package tk.sadbuttrue.lebuget.microservice.token

import reactivemongo.api.{DefaultDB, MongoDriver}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, Macros}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by true on 03/10/2016.
  */
case class Token(value: String, validTo: Long, identityId: Long, authMethods: Set[String])

class Repository(implicit ec: ExecutionContext) extends Config {
  def insertToken(token: Token): Future[Token] = tokens.flatMap(_.insert(token).map(_ => token))

  def updateTokenByValue(value: String, token: Token): Future[Int] = tokens.flatMap(_.update(BSONDocument("value" -> value), token).map(_.n))

  def deleteTokenByValue(value: String): Future[Int] = tokens.flatMap(_.remove(BSONDocument("value" -> value)).map(_.n))

  def findValidTokenByValue(value: String): Future[Option[Token]] = {
    tokens.flatMap(_.find(BSONDocument("value" -> value, "validTo" -> BSONDocument("$gt" -> System.currentTimeMillis()))).cursor[Token]().headOption)
  }

  def addMethodToValidTokenByValue(value: String, method: String): Future[Option[Token]] = {
    tokens.flatMap(_.update(BSONDocument("value" -> value), BSONDocument("$addToSet" -> BSONDocument("authMethods" -> method))).flatMap { lastError =>
      if (lastError.n > 0) findValidTokenByValue(value) else Future.successful(None)
    })
  }

  private implicit val tokenHandler = Macros.handler[Token]
  private val driver = MongoDriver()
  private val connection = driver.connection(nodes = List(mongoHost))
  private val database: Future[DefaultDB] = connection.database(mongoDb)
  if (!(mongoUser isEmpty)) connection.authenticate(mongoDb, mongoUser, mongoPassword)
  private val tokens: Future[BSONCollection] = database.map(_.collection("tokens"))
}
