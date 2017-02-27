package akka_mars_rover.gear

import akka_mars_rover.lib.Geo.CardinalDirection._
import akka_mars_rover.lib.Geo.Coordinates
import akka_mars_rover.lib.Grid
import akka_mars_rover.lib.Grid.Grid


class Gps{

  def nextSector(coord:Coordinates, direction:CardinalDirection) : Coordinates = {
    val nextNorth : (Coordinates) => Coordinates = c => c.copy( x = c.x - 1 )
    val nextSouth : (Coordinates) => Coordinates = c => c.copy( x = c.x + 1 )
    val nextWest  : (Coordinates) => Coordinates = c => c.copy( y = c.y - 1 )
    val nextEast  : (Coordinates) => Coordinates = c => c.copy( y = c.y + 1 )

    direction match {
      case NORTH => nextNorth(coord)
      case EAST  => nextEast(coord)
      case SOUTH => nextSouth(coord)
      case WEST  => nextWest(coord)
      case _   => throw new Exception /* TODO */
    }

  }

}
