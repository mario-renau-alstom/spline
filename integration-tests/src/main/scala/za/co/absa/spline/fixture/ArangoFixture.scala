package za.co.absa.spline.fixture

import java.net.URI

import com.arangodb.ArangoDatabase
import za.co.absa.spline.persistence.{ArangoFactory, ArangoInit}
import za.co.absa.spline.fixture.SystemConfigWrapper._

object ArangoFixture {

  val arangoUri: String = getProperty("test.spline.arangodb.url", "http://root:root@localhost:8529/unit-test")
  def createArangodb(): ArangoDatabase = ArangoFactory.create(new URI(arangoUri))

  def dropAndInit(arangodb: ArangoDatabase): Unit = {
    val db = arangodb
    if (db.exists()) {
      db.drop()
    }
    ArangoInit.initialize(db, dropIfExists = true)
  }
}
