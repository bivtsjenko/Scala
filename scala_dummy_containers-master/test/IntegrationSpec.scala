package controllers

import java.io.IOException
import java.net.ServerSocket

import com.github.simplyscala.{MongoEmbedDatabase, MongodProps}
import helpers.Awaiting
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}
import repos.WidgetRepoImpl

import scala.io.Source

class IntegrationSpec extends PlaySpec with GuiceOneServerPerSuite with MongoEmbedDatabase with BeforeAndAfterAll with Awaiting {
  import play.api.Application

  val mongoPort: Int = findRandomOpenPortOnAllLocalInterfaces
  var mongoProps: MongodProps = _

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .configure("mongodb.uri" -> s"mongodb://localhost:$mongoPort/datapool", "authentication.enabled" -> "false")
    .build()

  override protected def beforeAll(): Unit = {
    mongoProps = mongoStart(mongoPort)
  }

  override protected def afterAll(): Unit = mongoStop(mongoProps)

  @throws[IOException]
  private def findRandomOpenPortOnAllLocalInterfaces = {
    val socket = new ServerSocket(0)
    try
      socket.getLocalPort
    finally if (socket != null) socket.close()
  }

  "Application" should {

    val wsClient: WSClient = app.injector.instanceOf[WSClient]

    "Container endpoint" should {
      "Store a movement" in {
        val myPublicAddress = s"http://localhost:$port/api/widget"

        val source = Source.fromFile(this.getClass.getResource("/recourses/json_inter.json").getFile).getLines().mkString

        val data = Json.parse(source)

        val repo: WidgetRepoImpl = app.injector.instanceOf[WidgetRepoImpl]

        val response: WSResponse = await[WSResponse](wsClient.url(myPublicAddress).post(data))
        response.status must be(200)
        println(response.body)
        response.body must include("Record added")

        val uri = myPublicAddress + "/POA74564742/2018-12-01T08:39:01"
        println(uri)
        val queryRespone = await[WSResponse](wsClient.url(uri).get())
        println(queryRespone)
        println(queryRespone.body)
        response.body must not be(empty)
      }
    }
  }
}