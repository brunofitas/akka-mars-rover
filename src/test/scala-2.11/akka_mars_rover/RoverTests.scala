package akka_mars_rover

import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestProbe, TestKitBase}
import akka_mars_rover.Rover._
import akka_mars_rover.Rover.RoverStatus._
import akka_mars_rover.lib.Com.{CommandError, CommandSuccess}
import akka_mars_rover.lib.Geo.{Position, Coordinates}
import akka_mars_rover.lib.Geo.Direction._
import akka_mars_rover.lib.Geo.CardinalDirection._
import akka_mars_rover.lib.Grid

import org.scalatest.{Matchers, FlatSpec}

class RoverTests extends FlatSpec with Matchers with TestKitBase{

  implicit lazy val system = ActorSystem()

  val probe = TestProbe()
  val rover = system.actorOf(Props( new Rover(Grid())))
  val rover2 = system.actorOf(Props( new Rover(Grid())))


  behavior of "Rover"

  "RoverDeploy" must "deploy a rover with a valid position" in {
    probe.send(rover, Deploy(Position(Coordinates(0,0), SOUTH)))
    probe.expectMsg(CommandSuccess(201, Some("Rover deployed")))
  }

  it must "return an error when deploying the same object" in {
    probe.send(rover, Deploy(Position(Coordinates(0,0), SOUTH)))
    probe.expectMsg(CommandError(400,Some("Object already exists")))
  }

  it must "return an error when deploying outside the grid" in {
    probe.send(rover2, Deploy(Position(Coordinates(-10,0), SOUTH)))
    probe.expectMsg(CommandError(400,Some("Invalid position")))
  }

  it must "return error when deploying on a occupied sector"in {
    probe.send(rover2, Deploy(Position(Coordinates(0,0), SOUTH)))
    probe.expectMsg(CommandError(400,Some("Occupied position")))
  }


  "RoverInfoRequest" must "return rover position" in {
    probe.send(rover, InfoRequest())
    probe.expectMsg(InfoResponse(STOPPED,Position(Coordinates(0,0),SOUTH)))
  }


  "Directions" must "run directional commands" in {

    println("Rover 1 is at (0,0) SOUTH")
    probe.send(rover, InfoRequest())
    probe.expectMsg(InfoResponse(STOPPED, Position(Coordinates(0,0), SOUTH)))


    println("Sending directions to ROVER 1")
    probe.send(rover, Execute(List[Direction](LEFT, RIGHT, FORWARD, LEFT, LEFT, RIGHT, FORWARD, BACKWARD)))
    probe.expectMsg(CommandSuccess(200))


    Thread sleep 10000
    println("ROVER 1 stops at (1,3) WEST")
    probe.send(rover, InfoRequest())
    probe.expectMsg(InfoResponse(STOPPED, Position(Coordinates(1,3), WEST)))


    println("ROVER 2 is deployed at (0,3) SOUTH")
    probe.send(rover2, Deploy(Position(Coordinates(0,3), SOUTH)))
    probe.expectMsg(CommandSuccess(201, Some("Rover deployed")))


    println("ROVER 2 is sent forward to (1,3) SOUTH")
    probe.send(rover2, Execute(List[Direction](FORWARD)))
    probe.expectMsg(CommandSuccess(200))


    println("ROVER 2 has to wait since (1,3) is occupied ")
    Thread.sleep(2000)
    probe.send(rover2, InfoRequest())
    probe.expectMsg(InfoResponse(WAITING, Position(Coordinates(0,3), SOUTH), Some(List(FORWARD))))

    println("ROVER 1 gets out of the way")
    probe.send(rover, Execute(List[Direction](FORWARD)))
    probe.expectMsg(CommandSuccess(200))

    println("ROVER 1 is now at (1,2) WEST")
    Thread sleep 2000
    probe.send(rover, InfoRequest())
    probe.expectMsg(InfoResponse(STOPPED, Position(Coordinates(1,2), WEST)))

    println("ROVER 2 is now at (1,3) SOUTH")
    Thread.sleep(2000)
    probe.send(rover2, InfoRequest())
    probe.expectMsg(InfoResponse(STOPPED, Position(Coordinates(1,3), SOUTH)))

  }




}
