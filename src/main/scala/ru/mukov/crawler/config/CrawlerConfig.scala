package ru.mukov.crawler.config

import scala.concurrent.duration.FiniteDuration
import pureconfig._
import pureconfig.generic.auto._

case class CrawlerConfig(
                          timeout: FiniteDuration,
                          maxParallel: Int,
                          userAgent: String,
                          maxBodySize: Long
                        )

object CrawlerConfig {
  def load(): CrawlerConfig = ConfigSource.default.at("crawler").loadOrThrow[CrawlerConfig]
}