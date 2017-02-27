package akka_mars_rover.lib

object Com {

  case class ExecException(code:Int, msg:String) extends Exception(msg)
  case class CommandError(code:Int, msg:Option[String] = None)
  case class CommandSuccess(code:Int, msg:Option[String] = None)

}
