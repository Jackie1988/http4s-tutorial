package server

import cats.data.Kleisli
import org.http4s._
import cats.effect.IO
import org.http4s.{HttpRoutes, Request}
import com.typesafe.scalalogging.StrictLogging

class NewService extends StrictLogging {

  def myMiddle(service: HttpRoutes[IO]): HttpRoutes[IO] = Kleisli { req: Request[IO] =>
    service(req).map {
      case Status.Successful(resp) =>
        logger.info("Request was successful")
        resp
      case resp =>
        resp
    }
  }

}
