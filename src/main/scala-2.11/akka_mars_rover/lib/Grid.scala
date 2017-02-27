package akka_mars_rover.lib

import akka.actor.ActorRef
import akka_mars_rover.lib.Com.ExecException
import scala.collection.mutable.{Map => MMap, ListBuffer}
import akka_mars_rover.lib.Geo.{Position, Coordinates}

import scala.util.Try


object Grid {


  class Grid(width: Int, height: Int) {

    val objects: MMap[ActorRef, Position] =
      MMap.empty[ActorRef, Position]


    def register(obj:ActorRef, pos:Position): Unit = {
      if(objects.contains(obj)) throw new ExecException(400, "Object already exists")
      if(!validCoordinates(pos.coord)) throw new ExecException(400, "Invalid position")
      if(occupiedPosition(pos.coord)) throw new ExecException(400, "Occupied position")
      Try(objects += (obj -> pos)).getOrElse(throw new ExecException(500, "Error registering object"))
    }

    def unregister(obj:ActorRef): Unit =
      Try(objects.remove(obj)).getOrElse(throw new ExecException(500, "Error unregistering object"))

    def validCoordinates(c:Coordinates) : Boolean =
      c.x >= 0 && c.x < width && c.y >= 0 && c.y < height

    def emptyPosition(pos:Position) : Boolean =
      ! objects.values.map(_.coord).exists(_ == pos.coord)

    def occupiedPosition(c:Coordinates) : Boolean =
      objects.values.map(_.coord).exists(_ == c)

    def update(obj:ActorRef, pos: Position): Unit =
      Try(objects(obj) = pos).getOrElse(throw new ExecException(500, "Error updating object"))



  }

  object G100{
    private val instance = new Grid(100, 100)
    def apply() = instance
  }

  object G200{
    private val instance = new Grid(200, 200)
    def apply() = instance
  }


  private val instance = G100()

  def apply() = instance


}


