package models

import cats.effect._, org.http4s._, org.http4s.dsl.io._

case class Tweet(id: Int, message: String)
// defined class Tweet

object Tweet {

  implicit def tweetEncoder: EntityEncoder[IO, Tweet] = ???
  // tweetEncoder: org.http4s.EntityEncoder[cats.effect.IO,Tweet]

  implicit def tweetsEncoder: EntityEncoder[IO, Seq[Tweet]] = ???
  // tweetsEncoder: org.http4s.EntityEncoder[cats.effect.IO,Seq[Tweet]]

  def getTweet(tweetId: Int): IO[Tweet] = ???
  // getTweet: (tweetId: Int)cats.effect.IO[Tweet]

  def getPopularTweets(): IO[Seq[Tweet]] = ???
  // getPopularTweets: ()cats.effect.IO[Seq[Tweet]]

  val tweetService = HttpRoutes.of[IO] {
    case GET -> Root / "tweets" / "popular" =>
      getPopularTweets().flatMap(Ok(_))
    case GET -> Root / "tweets" / IntVar(tweetId) =>
      getTweet(tweetId).flatMap(Ok(_))
  }
}


