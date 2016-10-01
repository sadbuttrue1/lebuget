package tk.sadbuttrue.lebuget.microservice.identity

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
/**
  * Created by true on 29/09/2016.
  */
object IdentityManager extends App with Config with JsonProtocols {
  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher = actorSystem.dispatcher

  val repository = new Repository
  val service = new Service(repository)

  Http().bindAndHandle(interface = interface, port = port, handler = {
    logRequestResult("identity-manager") {
      path("identities") {
        pathEndOrSingleSlash {
          post {
            complete {
              Created -> service.crateIdentity
            }
          } ~
            get {
              complete {
                service.listAllIdentities
              }
            }
        }
      }
    }
  })
}