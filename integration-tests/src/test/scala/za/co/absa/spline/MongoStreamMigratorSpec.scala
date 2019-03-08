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

package za.co.absa.spline

import org.scalatest._
import za.co.absa.spline.fixture.ArangoFixture.{arangoUri, createArangodb}
import za.co.absa.spline.fixture.LineageFixture.fiveOpsLineage
import za.co.absa.spline.fixture.MongoFixture.{lineageWriter, mongoUri}
import za.co.absa.spline.fixture.{ArangoFixture, MongoFixture}
import za.co.absa.spline.migrator.MongoStreamMigrator

import scala.concurrent.Future

/**
  * Ignored since times out in TC for unknown reason. Possibly TC is too slow to finish on time.
  */
class MongoStreamMigratorSpec extends AsyncFunSpec with Matchers with BeforeAndAfterEach {

  import scala.concurrent.ExecutionContext.Implicits._

  private val lineage = fiveOpsLineage()
  private val arangodb = createArangodb()
  private val mongoConnection = MongoFixture.createMongoConnection()

  describe("migration tool test") {
    it("stream new lineages to db") {
      val streaming = Future { new MongoStreamMigrator(mongoUri, arangoUri).start() }
      Thread.sleep(10000)
      lineageWriter.store(lineage).map(_ => {
        var count = 0
        do {
          count = count + 1
          Thread.sleep(1000)
          println(s"Looking for lineage with id ${lineage.id}")
        } while(!isLineageStoredInArango(lineage.id) && count < 90)
        isLineageStoredInArango(lineage.id) shouldBe true
      })
    }
  }

  private def isLineageStoredInArango(lineageId: String) =
    arangodb
      .collection("execution")
      .documentExists(lineage.id.replaceFirst("ln_", ""))

  override protected def beforeEach(): Unit = dropAndInitDbs()

  override protected def afterEach(): Unit = dropAndInitDbs()

  private def dropAndInitDbs(): Unit = {
    ArangoFixture.dropAndInit(arangodb)
    MongoFixture.dropDb(mongoConnection)
  }

}

