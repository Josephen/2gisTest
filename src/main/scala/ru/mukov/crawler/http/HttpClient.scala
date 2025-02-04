package ru.mukov.crawler.http

import cats.effect.IO
import org.http4s.client.Client
import org.http4s._
import org.typelevel.ci._
import ru.mukov.crawler.config.CrawlerConfig
import org.typelevel.log4cats.slf4j.Slf4jLogger

class HttpClient(client: Client[IO], config: CrawlerConfig) {

  private val logger = Slf4jLogger.getLogger[IO]

  def fetch(url: String): IO[String] = {
    val uriEither = Uri.fromString(url)
    uriEither.fold(
      err => IO.raiseError(new Exception(s"Invalid URL: $url, error: ${err.sanitized}")),
      validUri => {
        val request = Request[IO](
          Method.GET,
          validUri
        ).withHeaders(
          Header.Raw(ci"User-Agent", config.userAgent),
          Header.Raw(ci"Accept", "text/html")
        )

        logger.debug(s"Fetching $url") >>
          client.run(request).use { response =>
              if (response.status.isSuccess) {
                response.bodyText
                  .take(config.maxBodySize)
                  .compile
                  .string
                  .flatTap(body => logger.debug(s"Received response of length=${body.length} for $url"))
              } else {
                IO.raiseError(new Exception(s"HTTP Error: ${response.status} for $url"))
              }
            }
            .timeout(config.timeout)
      }
    )
  }
}
