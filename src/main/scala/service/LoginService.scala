package service

import com.typesafe.scalalogging.LazyLogging
import database.Database

class LoginService(implicit database: Database) extends LazyLogging {
  def authenticate(username: String, password: String): Int = {
    try {
      val user = database.getUser(username)
      if (user.password == password) {
        logger.debug(s"Authenticated user $username with id ${user.driverID}")
        user.driverID
      }
      else {
        logger.debug(s"Wrong password supplied for user $username")
        -1
      }
    }
    catch {
      case e: NoSuchElementException => {
        logger.debug(s"Couldn't find user $username")
        -1
      }
    }

  }
}
