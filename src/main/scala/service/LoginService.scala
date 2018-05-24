package service

import com.typesafe.scalalogging.LazyLogging
import database.Database

class LoginService(implicit database: Database) extends LazyLogging {
  def authenticate(username: String, password: String): Int = {
    val dbUser = database.getUser(username)
    dbUser match {
      case None => {
        logger.debug(s"Could not find user $username")
        -1
      }
      case Some(user) => {
        if (user.password == password) {
          logger.debug(s"Authenticated user $username with id ${user.driverID}")
          user.driverID
        }
        else {
          logger.debug(s"Wrong password supplied for user $username")
          -1
        }
      }
    }
  }
}
