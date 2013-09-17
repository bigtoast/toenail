package toenail

import akka.io.IO
import spray.can.Http
import akka.actor._
import spray.http._
import HttpMethods._


object Main extends App {

  implicit val system = ActorSystem()

  lazy val handler = system.actorOf(Props[EchoActor], name = "handler")

  IO(Http) ! Http.Bind(handler, interface = "localhost", port = 8080)

}

class EchoActor extends Actor {
	def receive = {
    case _: Http.Connected => sender ! Http.Register(self)

		case r @ HttpRequest(GET, _, _, _, _) =>
      	sender ! HttpResponse(entity = "Yo")
	}
}