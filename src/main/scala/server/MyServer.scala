package server

import cats.effect._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import cats.implicits._
import io.circe.Decoder.Result
//import org.http4s.circe._
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import models.Tweet.tweetService
import routes.MyHttpRoutes
import org.http4s.circe.CirceEntityCodec._

import scala.concurrent.ExecutionContext.Implicits.global

case class User(name: String)
case class Hello(user: User)

class MyServer {

  val myHttpRoutes = new MyHttpRoutes

  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO] = IO.timer(global)

  implicit def userJsonDecoder: Decoder[User] = new Decoder[User] {
    override def apply(c: HCursor): Result[User] =
      for {
        name <- c.downField("name").as[String]
      } yield User(name)
  }

  implicit def helloJsonDecoder: Decoder[Hello] = new Decoder[Hello] {
    override def apply(c: HCursor): Result[Hello] =
      for {
        user <- c.downField("user").as[User]
      } yield Hello(user)
  }

  implicit def userJsonEncoder: Encoder[User] =
    Encoder.instance { user: User =>
      Json.obj("name" -> Json.fromString(user.name))
    }

  implicit def helloJsonEncoder: Encoder[Hello] =
    Encoder.instance { hello: Hello =>
      Json.obj("hello" ->  hello.user.asJson)
    }

  val jsonApp = HttpRoutes.of[IO] {
    case req @ POST -> Root / "hello" =>
      for {
        // Decode a User request
        user <- req.as[User]
        // Encode a hello response
        resp <- Ok(Hello(user).asJson)
      } yield (resp)
  }.orNotFound

  import org.http4s.server.blaze._
  val server = BlazeServerBuilder[IO].bindHttp(8080).withHttpApp(jsonApp).resource
  val fiber = server.use(_ => IO.never).start.unsafeRunSync()

  /////////////////
  val services = tweetService <+> myHttpRoutes.helloWorldService
  // services: cats.data.Kleisli[[β$0$]cats.data.OptionT[cats.effect.IO,β$0$],org.http4s.Request[cats.effect.IO],org.http4s.Response[cats.effect.IO]] = Kleisli(cats.data.KleisliSemigroupK$$Lambda$20326/1678790828@6a90aeac)

  val httpApp = Router("/" -> myHttpRoutes.helloWorldService, "/api" -> services).orNotFound
  // httpApp: cats.data.Kleisli[cats.effect.IO,org.http4s.Request[cats.effect.IO],org.http4s.Response[cats.effect.IO]] = Kleisli(org.http4s.syntax.KleisliResponseOps$$Lambda$20337/1801349716@4e861a4e)

  val serverBuilder = BlazeServerBuilder[IO].bindHttp(8081, "localhost").withHttpApp(httpApp)
  // serverBuilder: org.http4s.server.blaze.BlazeServerBuilder[cats.effect.IO] = org.http4s.server.blaze.BlazeServerBuilder@6940cad7

}
