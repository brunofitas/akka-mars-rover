package akka_mars_rover


import akka.actor.ActorSystem
import akka.testkit.TestKitBase
import akka_mars_rover.gear.Gps
import akka_mars_rover.lib.Geo.CardinalDirection._
import akka_mars_rover.lib.Geo.Coordinates
import org.scalatest.{FlatSpec, Matchers}

class GpsTests extends FlatSpec with Matchers with TestKitBase{

  implicit lazy val system = ActorSystem()


  "nextSector.NORTH" must "be the sector above even if negative" in {
    new {} with Gps {
      assert( nextSector(Coordinates(0,0), NORTH) == Coordinates(-1,0))
      assert( nextSector(Coordinates(1,1), NORTH) == Coordinates(0,1))
      assert( nextSector(Coordinates(2,2), NORTH) == Coordinates(1,2))
      assert( nextSector(Coordinates(2,1), NORTH) == Coordinates(1,1))
    }
  }

  "nextSector.EAST" must "be the sector on the right" in {
    new {} with Gps {
      assert( nextSector(Coordinates(1,1), EAST) == Coordinates(1,2))
      assert( nextSector(Coordinates(2,2), EAST) == Coordinates(2,3))
      assert( nextSector(Coordinates(2,1), EAST) == Coordinates(2,2))
    }
  }

  "nextSector.SOUTH" must "be the sector under" in {
    new {} with Gps {
      assert( nextSector(Coordinates(1,1), SOUTH) == Coordinates(2,1))
      assert( nextSector(Coordinates(2,2), SOUTH) == Coordinates(3,2))
      assert( nextSector(Coordinates(2,1), SOUTH) == Coordinates(3,1))
    }
  }

  "nextSector.WEST" must "be the sector on the left" in {
    new {} with Gps {
      assert( nextSector(Coordinates(1,1), WEST) == Coordinates(1,0))
      assert( nextSector(Coordinates(2,2), WEST) == Coordinates(2,1))
      assert( nextSector(Coordinates(2,1), WEST) == Coordinates(2,0))
    }
  }

  "nextSector" must "be the next sector, according to direction" in {
    new {} with Gps {
      assert( nextSector(Coordinates(1,1), NORTH) == Coordinates(0,1))
      assert( nextSector(Coordinates(1,1), EAST) == Coordinates(1,2))
      assert( nextSector(Coordinates(1,1), SOUTH) == Coordinates(2,1))
      assert( nextSector(Coordinates(1,1), WEST) == Coordinates(1,0))
    }
  }


}
