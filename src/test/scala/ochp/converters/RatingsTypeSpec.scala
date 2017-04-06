package com.newmotion.ochp
package converters

import RatingsConverter._
import api._

class RatingsTypeSpec extends Spec {
  "guaranteed power may be omitted" >> {
    val expected = Ratings(22f, None, Some(220))

    fromOchp(toOchp(expected)) mustEqual expected
  }

  "nominal voltage may be omitted" >> {
    val expected = Ratings(22f, Some(22f), None)

    fromOchp(toOchp(expected)) mustEqual expected
  }
}
