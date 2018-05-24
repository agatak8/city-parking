package domain

// in a real system, password are not stored as plaintext
final case class User(username: String, password: String, driverID: Int)