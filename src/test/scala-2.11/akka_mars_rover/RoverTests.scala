package akka_mars_rover

import akka.actor.{Props, ActorSystem, ActorRef}
import akka.testkit.{TestProbe, TestKitBase, TestActorRef}
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

  "RoverDeploy" must "deploy a rover" in {
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

    // Rover 1 at (0,0) South
    probe.send(rover, InfoRequest())
    probe.expectMsg(InfoResponse(STOPPED, Position(Coordinates(0,0), SOUTH)))

    // Rover 1 - execute directions
    probe.send(rover, Execute(List[Direction](LEFT, RIGHT, FORWARD, LEFT, LEFT, RIGHT, FORWARD, BACKWARD)))
    probe.expectMsg(CommandSuccess(200))


    // Rover 1 stops at (1,3) West
    Thread sleep 10000
    probe.send(rover, InfoRequest())
    probe.expectMsg(InfoResponse(STOPPED, Position(Coordinates(1,3), WEST)))


    // Rover 2 is deployed at (0,3) South
    probe.send(rover2, Deploy(Position(Coordinates(0,3), SOUTH)))
    probe.expectMsg(CommandSuccess(201, Some("Rover deployed")))

    // Rover 2 is sent forward to (1,3) South
    probe.send(rover2, Execute(List[Direction](FORWARD)))
    probe.expectMsg(CommandSuccess(200))

    // Rover 2 is waiting since (1,3) is occupied
    Thread.sleep(2000)
    probe.send(rover2, InfoRequest())
    probe.expectMsg(InfoResponse(WAITING, Position(Coordinates(0,3), SOUTH), Some(List(FORWARD))))

    // Rover 1 gets out of the way
    probe.send(rover, Execute(List[Direction](FORWARD)))
    probe.expectMsg(CommandSuccess(200))

    // Rover 1 is now at (1,2) West
    Thread sleep 2000
    probe.send(rover, InfoRequest())
    probe.expectMsg(InfoResponse(STOPPED, Position(Coordinates(1,2), WEST)))

    // Rover 2 is now at (1,3) South
    Thread.sleep(2000)
    probe.send(rover2, InfoRequest())
    probe.expectMsg(InfoResponse(STOPPED, Position(Coordinates(1,3), SOUTH)))

  }




}
