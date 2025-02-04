package ru.mukov.crawler

import cats.effect._
import cats.syntax.all._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.middleware.{CORS, ErrorHandling}
import ru.mukov.crawler.config.CrawlerConfig
import ru.mukov.crawler.http.{CrawlerRoutes, HttpClient}
import ru.mukov.crawler.service.CrawlerService
import scala.concurrent.ExecutionContext

object Main extends IOApp {

  private def makeThreadPool( threads: Int): Resource[IO, ExecutionContext] = {
    Resource
      .make {
        IO {
          val threadFactory = java.util.concurrent.Executors.defaultThreadFactory()
          java.util.concurrent.Executors.newFixedThreadPool(threads, threadFactory)
        }
      } { es =>
        IO(es.shutdown())
      }
      .map(ExecutionContext.fromExecutorService)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val config = CrawlerConfig.load()

    val resources: Resource[IO, (ExecutionContext, ExecutionContext)] =
      (makeThreadPool(4), makeThreadPool(4)).tupled

    resources.use { case (clientEC, serverEC) =>
      BlazeClientBuilder[IO].withExecutionContext(clientEC).resource.use { blazeClient =>
        val httpClient = new HttpClient(blazeClient, config)
        val service    = new CrawlerService(httpClient, config)
        val routes     = new CrawlerRoutes(service).routes
        val finalApp   = CORS.policy(ErrorHandling(routes)).orNotFound

        BlazeServerBuilder[IO].withExecutionContext(serverEC)
          .bindHttp(port = 8080, host = "0.0.0.0")
          .withHttpApp(finalApp)
          .serve
          .compile
          .drain
          .as(ExitCode.Success)
      }
    }
  }
}
