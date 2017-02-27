package akka_mars_rover

import akka_mars_rover.gear.Compass
import org.scalatest.{FlatSpec, Matchers}
import akka_mars_rover.lib.Geo.Direction._
import akka_mars_rover.lib.Geo.CardinalDirection._


class CompassTests extends FlatSpec with Matchers{


  "compass" must "initialize calibrated to north" in {
    new {} with Compass {
      assert(getDirection(current) == NORTH)
    }

  }

  "preview.right" must "preview the compass clockwise" in {
    new {} with Compass {
      println("\nNorth:\n")
      printCompass(current)
      assert(getDirection(current) == NORTH)

      val east = preview(current, RIGHT)
      assert(getDirection(east) == EAST)
      assert(getDirection(current) == NORTH)
      println("\nRotating right:\n")
      printCompass(east)

      val south = preview(east, RIGHT)
      assert(getDirection(south) == SOUTH)
      assert(getDirection(current) == NORTH)
      println("\nRotating right:\n")
      printCompass(south)

      val west = preview(south, RIGHT)
      assert(getDirection(west) == WEST)
      assert(getDirection(current) == NORTH)
      println("\nRotating right:\n")
      printCompass(west)

      val north = preview(west, RIGHT)
      assert(getDirection(north) == NORTH)
      println("\nRotating right:\n")
      printCompass(north)

    }
  }



  "preview.left" must "preview the compass anti clockwise" in {
    new {} with Compass {

      assert(getDirection(current) == NORTH)

      val west = preview(current, LEFT)
      assert(getDirection(west) == WEST)
      assert(getDirection(current) == NORTH)
      println("\nRotating left:\n")
      printCompass(west)

      val south = preview(west, LEFT)
      assert(getDirection(south) == SOUTH)
      assert(getDirection(current) == NORTH)
      println("\nRotating left:\n")
      printCompass(south)

      val east = preview(south, LEFT)
      assert(getDirection(east) == EAST)
      assert(getDirection(current) == NORTH)
      println("\nRotating left:\n")
      printCompass(east)

      val north = preview(east, LEFT)
      assert(getDirection(north) == NORTH)
      println("\nRotating left:\n")
      printCompass(north)
    }

  }

  "preview.back" must "preview the compass backwards" in {
    new {} with Compass {

      assert(getDirection(current) == NORTH)

      val south = preview(current, BACKWARD)
      assert(getDirection(south) == SOUTH)
      assert(getDirection(current) == NORTH)

      val west = preview(south, RIGHT)
      assert(getDirection(west) == WEST)
      assert(getDirection(current) == NORTH)

      val east = preview(west, BACKWARD)
      assert(getDirection(east) == EAST)
      assert(getDirection(current) == NORTH)
    }
  }

  "calibrateCompass" must "set the compass to a specific direction" in {
    new {} with Compass {
      assert(getDirection(current) == NORTH)

      calibrate(SOUTH)
      assert(getDirection(current) == SOUTH)

      calibrate(EAST)
      assert(getDirection(current) == EAST)

      calibrate(WEST)
      assert(getDirection(current) == WEST)
    }
  }

  "setCompass" must "update the compass direction" in {
    new {} with Compass {
      assert(getDirection(current) == NORTH)

      val west = preview(current, LEFT)
      assert(getDirection(west) == WEST)

      assert(getDirection(current) == NORTH)

      set(west)
      assert(getDirection(current) == WEST)

    }
  }

}
