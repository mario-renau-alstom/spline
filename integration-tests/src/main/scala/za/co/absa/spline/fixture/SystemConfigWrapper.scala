package za.co.absa.spline.fixture

import org.apache.commons.configuration.SystemConfiguration

/**
  * Resolves String properties treating empty the same as null.
  * This avoids empty String value issues with providing properties via Maven.
  */
object SystemConfigWrapper {

  private val conf = new SystemConfiguration()

  def getProperty(name: String, default: String): String = {
    val value = conf.getString(name, default)
    if (value == "") {
      default
    } else {
      value
    }
  }

  def getRequiredProperty(name: String): String = {
    val value = getProperty(name, "")
    if (value == "") {
      throw new IllegalArgumentException(s"Required property $name has value $value.")
    } else {
      value
    }
  }

}
