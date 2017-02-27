package akka_mars_rover.gear

import akka_mars_rover.lib.Geo
import akka_mars_rover.lib.Geo.Direction._
import akka_mars_rover.lib.Geo.CardinalDirection._


class Compass {

  var current : Array[Array[CardinalDirection]] =
    Geo.COMPASS

  def calibrate(pointing:CardinalDirection = NORTH) = {
    pointing match {
      case NORTH => set(Geo.COMPASS)
      case EAST  => set(preview(Geo.COMPASS, RIGHT))
      case SOUTH => set(preview(Geo.COMPASS, BACKWARD))
      case WEST  => set(preview(Geo.COMPASS, LEFT))
      case _     => /* TODO */
    }
  }

  def set(matrix:Array[Array[CardinalDirection]]) : Unit =
    current = matrix

  def getDirection(matrix:Array[Array[CardinalDirection]]) : CardinalDirection =
    matrix(0)(1)

  def currentDirection : CardinalDirection =
    getDirection(current)


  def preview(matrix:Array[Array[CardinalDirection]], direction:Direction) : Array[Array[CardinalDirection]] = {
    val right : (Array[Array[CardinalDirection]], Int, Int) => CardinalDirection = (c, x, y) => c(y)(c.length - x - 1)
    val left  : (Array[Array[CardinalDirection]], Int, Int) => CardinalDirection = (c, x, y) => c(c.length - y - 1)(x)
    val back  : (Array[Array[CardinalDirection]], Int, Int) => CardinalDirection = (c, x, y) => c(c.length - x - 1)(c.length - y - 1)
    val none  : (Array[Array[CardinalDirection]], Int, Int) => CardinalDirection = (c, x, y) => c(x)(y)

    val translator = direction match {
      case FORWARD  => none
      case RIGHT    => right
      case BACKWARD => back
      case LEFT     => left
    }

    val out = Geo.COMPASS

    for (x <- matrix.indices) {
      for (y <- matrix.indices) {
        out(x)(y) = translator(matrix, x, y)
      }
    }
    out
  }

  def printCompass(comp:Array[Array[CardinalDirection]]) = {
    for(x <- comp.indices) {
      for (y <- comp.indices) {
        print(s"${comp(x)(y)} ")
      }
      println()
    }
  }

}
