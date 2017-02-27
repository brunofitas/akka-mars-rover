package akka_mars_rover

import akka_mars_rover.lib.Com.{CommandSuccess, ExecException, CommandError}

import scala.concurrent.duration._
import akka.actor.{ActorLogging, Actor}
import scala.util.{Failure, Success, Try}
import scala.collection.mutable.ListBuffer
import akka_mars_rover.Rover._
import akka_mars_rover.Rover.RoverStatus.RoverStatus
import akka_mars_rover.gear.{Gps, Compass}
import akka_mars_rover.lib.Geo.CardinalDirection.CardinalDirection
import akka_mars_rover.lib.Geo.Direction._
import akka_mars_rover.lib.Geo.{Coordinates, Position}
import akka_mars_rover.lib.Grid.Grid


class Rover(val grid:Grid) extends Actor with ActorLogging{

  import Rover.RoverStatus._
  import context.dispatcher

  private case class ProcessQueue()

  val gps : Gps = new Gps
  var currentPosition : Position = _
  var status : RoverStatus = INITIAL
  val compass : Compass = new Compass
  var dirBuffer : ListBuffer[Direction] = ListBuffer.empty[Direction]
  val speed = 1

  def scheduler = context.system.scheduler

  def receive = {


    case Deploy(pos:Position, directions:Option[List[Direction]]) =>
      Try(grid.register(self, pos)) match {
        case Failure(f) => f match {
          case e:ExecException =>
            sender ! CommandError(e.code, Some(e.msg))
          case other =>
            sender ! other.printStackTrace(); CommandError(500)  /* TODO */
        }

        case Success(s) =>
          currentPosition = pos
          compass.calibrate(pos.dir)
          dirBuffer.clear()
          status = STOPPED

          if(directions.nonEmpty)
            self ! Execute(directions.get)

          sender ! CommandSuccess(201, Some("Rover deployed"))
      }


    case Execute(com:List[Direction], run:Boolean) =>
      status match {
        case INITIAL =>
          sender ! CommandError(404, Some("Rover not deployed"))

        case STOPPED =>
          com.foreach(dirBuffer.append(_))
          if(run){
            status = MOVING
            self   ! ProcessQueue
          }
          sender ! CommandSuccess(200)

        case MOVING | WAITING  =>
          com.foreach(dirBuffer.append(_))
          sender ! CommandSuccess(200)

        case LOST => sender ! CommandError(404, Some("Rover is lost"))
      }


    case ProcessQueue =>
      dirBuffer.headOption match {
        case None =>
          status = STOPPED
        case Some(direction) =>

          if(status == MOVING || status == WAITING){

            val directionPreview : Array[Array[CardinalDirection]] =
              compass.preview(compass.current, direction)

            val cardinalDir : CardinalDirection =
              compass.getDirection(directionPreview)

            val nextCoord:Coordinates =
              gps.nextSector( currentPosition.coord, cardinalDir)

            grid.validCoordinates(nextCoord) match {
              case false =>
                status = LOST
              case true =>
                grid.occupiedPosition(nextCoord) match {
                  case true =>
                    status = WAITING
                  case false => {
                    status = MOVING
                    compass.current = directionPreview
                    currentPosition = Position(nextCoord, cardinalDir)
                    grid.update(self, currentPosition)
                    dirBuffer.remove(0)
                  }
                }
            }
            scheduler.scheduleOnce(speed seconds, self, ProcessQueue)
          }
      }


    case Stop =>
      status = STOPPED
      sender ! CommandSuccess(200)

    case Start =>
      status = MOVING
      self ! ProcessQueue
      sender ! CommandSuccess(200)

    case Reset =>
      dirBuffer.clear()
      status = STOPPED
      sender ! CommandSuccess(200)


    case req:InfoRequest =>
      status match {
        case INITIAL =>
          sender ! CommandError(404, Some("Rover not deployed"))
        case _ =>
          sender ! InfoResponse(status, currentPosition, if(dirBuffer.nonEmpty) Some(dirBuffer.toList) else None)
      }

    case _ =>
      sender ! CommandError(400, Some("Bad request"))
  }

}

object Rover {

  object RoverStatus extends Enumeration {
    type RoverStatus = Value
    val INITIAL = Value("INITIAL")  // Initial state
    val STOPPED = Value("STOPPED")  // Stopped
    val MOVING  = Value("MOVING")   // Moving
    val WAITING = Value("WAITING")  // Waiting for sector to clear
    val LOST    = Value("LOST")     // Coordinates are out of range
  }

  case class Deploy(pos:Position, directions:Option[List[Direction]] = None)
  case class Execute(com:List[Direction], run:Boolean = true)
  case class Stop()
  case class Start()
  case class Reset()
  case class InfoRequest()
  case class InfoResponse(status:RoverStatus, position:Position, buffer:Option[List[Direction]] = None)

}


