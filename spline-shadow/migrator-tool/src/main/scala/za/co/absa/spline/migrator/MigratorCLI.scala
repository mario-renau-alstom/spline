/*
 * Copyright 2017 ABSA Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package za.co.absa.spline.migrator

import java.net.URI

import za.co.absa.spline.common.SplineBuildInfo
import za.co.absa.spline.persistence.{ArangoFactory, ArangoInit}

import scala.concurrent.ExecutionContext.Implicits.global

object MigratorCLI extends App {

  val cliParser = new scopt.OptionParser[MigratorConfig]("migrator-tool") {
    head("Spline Migration Tool", SplineBuildInfo.version)

    (opt[String]('s', "source")
      valueName "<url>"
      text "MongoDB connection URL, where the old Spline data is located"
      required()
      action ((url, conf) => conf.copy(mongoConnectionUrl = url)))

    (opt[String]('t', "target")
      valueName "<url>"
      text "ArangoDB connection URL, where the new Spline data should be written to"
      required()
      action ((url, conf) => conf.copy(arangoConnectionUrl = url)))

    (opt[Int]('n', "batch-size")
      text s"Number of lineages per batch. (Default is ${MigratorConfig.empty.batchSize})"
      validate (x => if (x > 0) success else failure("<batch-size> should be a positive number"))
      action ((value, conf) => conf.copy(batchSize = value)))

    (opt[Int]('x', "batch-max")
      text s"Number of batches to process. Negative value means unbounded. (Default is ${MigratorConfig.empty.batchesMax})"
      action ((value, conf) => conf.copy(batchesMax = value)))

    (opt[Unit]('i', "init-arango-db")
      text s"Initialize Arango DB"
      action ((_, conf) => conf.copy(initializeArangodb = true)))

    (opt[Unit]('s', "stream-new-lineages")
      text s"Migrate in parallel all new lineages that will arrive after start of this tool."
      action ((_, conf) => conf.copy(streamNewLineages = true)))

    (opt[Unit]('f', "force")
      text s"In combination with option '-i' it removes existing Arango database before creating a new one"
      action ((value, conf) => conf.copy(removeExistingArangodb = true)))

    help("help").text("prints this usage text")
  }

  cliParser.parse(args, MigratorConfig.empty) match {
    case Some(config) =>
      initArangoIfConfigured(config)
      MigratorTool.migrate(config)
        .map(stats => println(s"DONE. Processed total: ${stats.processed} (of which failures: ${stats.failures})"))
    case None => cliParser.terminate(Left(""))
  }

  private def initArangoIfConfigured(config: MigratorConfig): Unit = {
    if (config.initializeArangodb) {
      val db = ArangoFactory.create(new URI(config.arangoConnectionUrl))
      ArangoInit.initialize(db, config.removeExistingArangodb)
    }
  }

}
