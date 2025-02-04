package ru.mukov.crawler.service

import cats.effect.IO
import cats.effect.implicits._
import cats.implicits._
import org.jsoup.Jsoup
import ru.mukov.crawler.config.CrawlerConfig
import ru.mukov.crawler.http.HttpClient
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger

class CrawlerService(client: HttpClient, config: CrawlerConfig) {

  private val logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  def crawl(urls: List[String]): IO[List[(String, String)]] =
    for {
      _ <- logger.info(s"Starting crawl for ${urls.size} URL(s).")
      results <- urls.distinct.parTraverseN(config.maxParallel) { url =>
        client.fetch(url)
          .flatMap { html =>
            val title = extractTitle(html)
            logger.debug(s"Fetched title '$title' for url=$url") *>
              IO.pure((url, title))
          }
          .handleErrorWith { e =>
            logger.error(e)(s"Error while fetching url=$url") *>
              IO.pure((url, s"Error on page loading: ${e.getMessage}"))
          }
      }
      _ <- logger.info(s"Finished crawl for ${urls.size} URL(s).")
    } yield results

  private def extractTitle(html: String): String = {
    val title = Jsoup.parse(html).title().trim
    if (title.nonEmpty) title else "No title"
  }
}
