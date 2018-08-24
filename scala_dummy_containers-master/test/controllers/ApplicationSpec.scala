package controllers

import org.scalatest.Matchers._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

class ApplicationSpec extends PlaySpec with GuiceOneAppPerTest with MockitoSugar {
  import play.api.Application

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .configure("authentication.enabled" -> "false")
    .build()

  "Routes" should {
    "Send 404 on a bad request" in {
      val result: Option[Future[Result]] = route(app, FakeRequest(GET, "/anan"))
      result.map(status) mustBe Some(404)
    }
  }

  "Home page" should {
    "Send 200 request" in {
      val home: Option[Future[Result]] = route(app, FakeRequest(GET, "/"))
      home.map(status) mustBe Some(200)
    }
  }

}