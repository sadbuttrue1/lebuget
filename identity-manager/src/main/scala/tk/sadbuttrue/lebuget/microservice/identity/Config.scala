package tk.sadbuttrue.lebuget.microservice.identity

import com.typesafe.config.ConfigFactory

/**
  * Created by true on 01/10/2016.
  */
trait Config {
  protected val config = ConfigFactory.load()
  protected val interface = config.getString("http.interface")
  protected val port = config.getInt("http.port")
  protected val dbUrl = config.getString("db.url")
  protected val dbUser = config.getString("db.user")
  protected val dbPassword = config.getString("db.password")
}
