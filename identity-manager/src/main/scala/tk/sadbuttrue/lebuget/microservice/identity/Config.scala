package tk.sadbuttrue.lebuget.microservice.identity

import com.typesafe.config.ConfigFactory

/**
  * Created by true on 01/10/2016.
  */
trait Config {
  val config = ConfigFactory.load()
  val interface = config.getString("http.interface")
  val port = config.getInt("http.port")
  val dbUrl = config.getString("db.url")
  val dbUser = config.getString("db.user")
  val dbPassword = config.getString("db.password")
}
