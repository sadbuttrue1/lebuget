package tk.sadbuttrue.lebuget.microservice.token

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Created by true on 03/10/2016.
  */
trait JsonProtocols extends DefaultJsonProtocol with SprayJsonSupport {
  protected implicit val tokenFormat = jsonFormat4(Token.apply)
  protected implicit val reloginRequestFormat = jsonFormat2(ReloginRequest.apply)
  protected implicit val loginRequestFormat = jsonFormat2(LoginRequest.apply)
}
