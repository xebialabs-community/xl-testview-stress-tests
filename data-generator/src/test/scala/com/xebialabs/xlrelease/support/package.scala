package com.xebialabs.xlrelease

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.time.{Minutes, Millis, Seconds, Span}
import org.scalatest._
import scala.concurrent.ExecutionContext.Implicits.global

package object support {

  implicit val executionContext = global

  trait UnitTestSugar extends FunSpecLike with Matchers with ScalaFutures with BeforeAndAfterAll with BeforeAndAfterEach {
    implicit override val patienceConfig = PatienceConfig(timeout = scaled(Span(60, Minutes)), interval = scaled(Span(50, Millis)))
  }
}