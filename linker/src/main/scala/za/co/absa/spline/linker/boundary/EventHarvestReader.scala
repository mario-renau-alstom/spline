/*
 * Copyright 2017 Barclays Africa Group Limited
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

package za.co.absa.spline.linker.boundary

import org.apache.commons.configuration.Configuration
import org.apache.spark.sql.{Dataset, Encoder, Encoders, SparkSession}
import org.apache.spark.sql.types.BinaryType
import za.co.absa.spline.linker.boundary.ReaderProperties._
import za.co.absa.spline.model.streaming.ProgressEvent

object EventHarvestReader {

  implicit val EventEncoder: Encoder[ProgressEvent] = Encoders.kryo[ProgressEvent]

  val deserializer = new JavaKafkaDeserializer[ProgressEvent]

  def apply(configuration: Configuration, sparkSession: SparkSession): Dataset[ProgressEvent] = {
    // FIXME encode ProgressEvent as Product while Operation and others using Kryo to allow Catalyst optimizations.
    import sparkSession.implicits._
    val keyValue = sparkSession
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", configuration.getString(harvesterServersProperty))
      .option("subscribe", configuration.getString(eventsTopicProperty, defaultEventsTopic))
      .option("startingOffsets", configuration.getString(harvesterStartingOffsetsProperty, defaultStartingOffsets))
      .load()
    keyValue
      .select('value.cast(BinaryType)).as[Array[Byte]]
      .map(deserializer.deserialize)
  }
}
