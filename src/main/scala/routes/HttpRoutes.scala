package routes

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import _root_.server.{Hello, User}
import _root_.server.MyServer
import io.circe.generic.auto._

import org.http4s.circe.CirceEntityCodec._

import scala.concurrent.ExecutionContext.Implicits.global

class MyHttpRoutes {

  val myServer = new MyServer

  val helloWorldService = HttpRoutes.of[IO] {
    case POST -> Root / "hello" / name =>
      //Ok(s"Hello, $name.")
    Ok(Hello(User(name)))
  }


}
