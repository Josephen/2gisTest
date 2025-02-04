package ru.mukov.crawler.http

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe.CirceEntityCodec._
import io.circe.generic.auto._
import ru.mukov.crawler.service.CrawlerService
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger

case class CrawlRequest(urls: List[String])
case class CrawlResponse(url: String, title: String)

class CrawlerRoutes(service: CrawlerService) {

  private val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      logger.info("Health-check endpoint called.")
      Ok("OK")

    case req @ POST -> Root / "crawl" =>
      for {
        crawlRequest <- req.as[CrawlRequest]
        _            <- logger.info(s"Received request for crawling ${crawlRequest.urls.size} URLs")
        results      <- service.crawl(crawlRequest.urls)
        response     =  results.map { case (url, title) =>
          CrawlResponse(url, title)
        }
        resp         <- Ok(response)
      } yield resp
  }
}
