package client

import org.http4s.client.blaze._
import cats.effect.{ConcurrentEffect, ExitCode, IO, IOApp}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import _root_.server.{Hello, User}
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._
import org.http4s.server.blaze.BlazeServerBuilder
import routes.MyHttpRoutes
import org.http4s.implicits._
import cats.implicits._
import _root_.server.NewService

import scala.concurrent.duration._

class MyClient {

  // Decode the Hello response
  def helloClient(name: String)(implicit concurrentEffect: ConcurrentEffect[IO]): IO[Hello] = {
    // Encode a User request
    val req = POST(User(name).asJson, uri"http://localhost:8080/hello/spock")
    // Create a client
    BlazeClientBuilder[IO](scala.concurrent.ExecutionContext.global).resource.use { httpClient =>
      // Decode a Hello response
      httpClient.expect[Hello](req)
    }
  }

}

object Main extends IOApp {
  
  override def run(args: List[String]): IO[ExitCode] = {
    val client = new MyClient
    val myHttpRoutes = new MyHttpRoutes
    val myNewService = new NewService
    val httpApp: HttpRoutes[IO] = myNewService.myMiddle(myHttpRoutes.helloWorldService)

    for {
      f <- BlazeServerBuilder[IO]
            .bindHttp(8080, "localhost")
            .withHttpApp(httpApp.orNotFound)
            .serve
            .compile
            .drain
            .as(ExitCode.Success)
            .start
      _ <- IO.sleep(1.seconds)
      _ <- client.helloClient("jackie")
    } yield ExitCode.Success
  }

}
