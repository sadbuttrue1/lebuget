package tk.sadbuttrue.lebuget.microservice.identity

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Created by true on 01/10/2016.
  */
trait JsonProtocols extends DefaultJsonProtocol with SprayJsonSupport{
  implicit val identityFormat = jsonFormat2(Identity.apply)
}
