import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.server.blaze.BlazeServerBuilder
import routes.MyHttpRoutes
import cats.implicits._
import client.MyClient
import org.http4s.implicits._

//object Main extends IOApp {
//
//  val myHttpRoutes = new MyHttpRoutes
//
//  def run(args: List[String]): IO[ExitCode] =
//    BlazeServerBuilder[IO].bindHttp(8080, "localhost").withHttpApp(myHttpRoutes.helloWorldService.orNotFound).serve.compile.drain.as(ExitCode.Success)
//
//}
