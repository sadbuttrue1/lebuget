package tk.sadbuttrue.lebuget.microservice.token

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by true on 03/10/2016.
  */
case class LoginRequest(identityId: Long, authMethod: String)

case class ReloginRequest(tokenValue: String, authMethod: String)

object TokenManager extends App with JsonProtocols with Config {
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  val repository = new Repository
  val service = new Service(repository)

  Http().bindAndHandle(interface = interface, port = port, handler = {
    logRequestResult("token-manager") {
      pathPrefix("tokens") {
        (post & pathEndOrSingleSlash & entity(as[LoginRequest])) { loginRequest =>
          complete {
            service.login(loginRequest).map(token => Created -> token)
          }
        } ~
          (patch & pathEndOrSingleSlash & entity(as[ReloginRequest])) { reloginRequest =>
            complete {
              service.relogin(reloginRequest).map[ToResponseMarshallable] {
                case Some(token) =>
                  OK -> token
                case None =>
                  NotFound -> "Token expired or not found"
              }
            }
          } ~
          (path(Segment) & pathEndOrSingleSlash) { tokenValue =>
            get {
              complete {
                service.findAndRefreshToken(tokenValue).map[ToResponseMarshallable] {
                  case Some(token) =>
                    OK -> token
                  case None =>
                    NotFound -> "Token expired or not found"
                }
              }
            } ~
              delete {
                complete {
                  service.logout(tokenValue)
                  OK
                }
              }
          }
      }
    }
  })
}
