package routes

import java.time.Year

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import _root_.server.{Hello, User}

import org.http4s.headers.`Cache-Control`
import org.http4s.CacheDirective.`no-cache`
import cats.data.NonEmptyList
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.CirceEntityCodec._

object NameVar {
  def unapply(name: String): Option[String] = {
    if (name.exists(_.isDigit)) {
      None
    } else {
      Some(name)
    }
  }
}

case class CountryCode(code: String)

object CountryCode {
  val acceptableCodes = Set("GB", "FR", "CY", "US")

  def apply(code: String): CountryCode =
    if(acceptableCodes.contains(code))
      new CountryCode(code)
    else CountryCode("GB")
  def unapply(arg: CountryCode): String =
    if(acceptableCodes.contains(arg.code))
      arg.code
    else "GB"

  implicit val countryCodeQueryParamDecoder: QueryParamDecoder[CountryCode] =
    QueryParamDecoder[String].map(s => CountryCode.apply(s))
}

class MyHttpRoutes {

  object CountryCodeQueryParamMatcher extends ValidatingQueryParamDecoderMatcher[CountryCode]("countryCode")

  implicit val yearQueryParamDecoder: QueryParamDecoder[Year] =
    QueryParamDecoder[Int].map(Year.of)

  object YearQueryParamMatcher extends ValidatingQueryParamDecoderMatcher[Year]("year")

  val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root / "say-hello" / name =>
      Ok(s"thank you $name")
    case GET -> Root / "name" / NameVar(name) =>
      Ok(s"Name $name is valid as there were no digits")
      // curl -X GET 'http://localhost:8080/name-and-cc/jackie?year=2019&countryCode=GB'
    case GET -> Root / "name-and-cc" / name :? YearQueryParamMatcher(yearValidated) +& CountryCodeQueryParamMatcher(countryCodeValidated) =>
      yearValidated.fold(
        parseFailures => BadRequest("unable to parse argument year"),
        year => countryCodeValidated.fold(
          parseFailures => BadRequest("unable to parse argument country code"),
          countryCode => Ok(s"Name is $name and their country code is: ${countryCode.code} and the year is $year",
            `Cache-Control`(NonEmptyList(`no-cache`(), Nil)))
        )
      )
      // curl -X POST http://localhost:8080/hello/jackie
    case POST -> Root / "hello" / name =>
      Ok(Hello(User(name)))
    case req @ POST -> Root / "hello2" =>
      for {
        // Decode a User request
        user <- req.as[User]
        // Encode a hello response
        //resp <- Ok(s"Received Json: ${req.body.asJson}, Hello response: ${Hello(User(user.name)).asJson}")
        resp <- Ok(s"Received Json: ${req.body}, Hello response: ${Hello(User(user.name)).asJson}")
      } yield resp
    case req @ POST -> Root / "hello3" =>
      for {
        // Decode a User request
        user <- req.as[User]
        // Encode a hello response
        //resp <- Ok(s"Received Json: ${req.body.asJson}, Hello response: ${Hello(User(user.name)).asJson}")
        resp <- Ok(s"Received Json: ${user.asJson}, Hello response: ${Hello(User(user.name)).asJson}")
      } yield resp

  }

}
