package akka_mars_rover.lib


object Geo{

  object Direction extends Enumeration {
    type Direction = Value
    val FORWARD  = Value("F")
    val RIGHT    = Value("R")
    val BACKWARD = Value("B")
    val LEFT     = Value("L")
  }

  object CardinalDirection extends Enumeration {
    type CardinalDirection = Value
    val NORTH     = Value("N")
    val NORTHEAST = Value("NE")
    val EAST      = Value("E")
    val SOUTHEAST = Value("SE")
    val SOUTH     = Value("S")
    val SOUTHWEST = Value("SW")
    val WEST      = Value("W")
    val NORTHWEST = Value("NW")
    val OBSERVER  = Value("-O-")
  }

  import CardinalDirection._

  def COMPASS : Array[Array[CardinalDirection]] = {
    Array[Array[CardinalDirection]](
      Array(NORTHWEST, NORTH, NORTHEAST ),
      Array(WEST,    OBSERVER,     EAST ),
      Array(SOUTHWEST, SOUTH, SOUTHEAST )
    )
  }


  case class Coordinates(x:Int, y:Int)

  case class Position(coord:Coordinates, dir:CardinalDirection)


}

