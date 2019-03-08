package za.co.absa.spline.fixture

import za.co.absa.spline.persistence.mongo.dao.{LineageDAOv3, LineageDAOv4, LineageDAOv5, MultiVersionLineageDAO}
import za.co.absa.spline.persistence.mongo.{MongoConnection, MongoConnectionImpl, MongoDataLineageWriter}
import za.co.absa.spline.fixture.SystemConfigWrapper._

object MongoFixture {

  def createMongoConnection() = new MongoConnectionImpl(mongoUri)

  private def dao = {
    val mongoConnection = createMongoConnection()
    new MultiVersionLineageDAO(
      new LineageDAOv3(mongoConnection),
      new LineageDAOv4(mongoConnection),
      new LineageDAOv5(mongoConnection))
  }

  val mongoUri: String = getProperty("test.spline.mongodb.url", "mongodb://localhost/integration-test")
  def lineageWriter: MongoDataLineageWriter = new MongoDataLineageWriter(dao)


  def dropDb(mongoConnection: MongoConnection): Unit = {
    for {
      collectionName <- mongoConnection.db.collectionNames
      if !(collectionName startsWith "system.")
      collection = mongoConnection.db(collectionName)
    } collection.drop()
  }
}
