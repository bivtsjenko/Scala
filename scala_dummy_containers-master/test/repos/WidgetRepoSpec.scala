package repos

import java.io.IOException
import java.net.ServerSocket
import java.sql.Timestamp

import com.github.simplyscala.{MongoEmbedDatabase, MongodProps}
import helpers.Awaiting
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json._

import scala.io.Source

/**
  * Class HigContainerModelSpec
  */
class HigContainerModelSpec extends WordSpec with GuiceOneServerPerSuite with MongoEmbedDatabase with BeforeAndAfterAll with Matchers with Awaiting {

  val mongoPort: Int = findRandomOpenPortOnAllLocalInterfaces
  val baseJson = Source.fromFile(this.getClass.getResource("/recourses/json_file.json").getFile).getLines().mkString

  import repos.TimeContainerModel._

  val data: Seq[ContainerFields] = Json.parse(baseJson).as[Seq[ContainerFields]]
  val repo: WidgetRepoImpl = app.injector.instanceOf(classOf[WidgetRepoImpl])

  var mongoProps: MongodProps = _

  override protected def afterAll(): Unit = mongoStop(mongoProps)

  override protected def beforeAll(): Unit = {
    mongoProps = mongoStart(mongoPort)
    fakeApplication()
    insertData()
  }

  def insertData(): Unit = {}

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .configure("mongodb.uri" -> s"mongodb://localhost:$mongoPort/datapool", "authentication.enabled" -> "false")
    .build()

  @throws[IOException]
  private def findRandomOpenPortOnAllLocalInterfaces = {
    val socket = new ServerSocket(0)
    try
      socket.getLocalPort
    finally if (socket != null) socket.close()
  }

  //
  "marshalling from source" should {
    "simple variant" in {
      data.head.containerNumber should be("POA74564742")
      data.head.timestamp.getTime should be(1543649941000l)
      data.head.containerType should be("W6O4")
      data.head.direction should be("IN")
      data.head.loaded should be(true)
      data.head.transportType should be("LARGE")
      data.head.identifier should be("6V2 29499")

    }
  }


    "query find_t" in {
      val timestamp = new Timestamp(System.currentTimeMillis())
      val containersFields = ContainerFields(timestamp, "HMCU8665487", "54MH", true, "LARGE", "OUT", "ENI 46861")
      await(repo.store(containersFields))

      val result = await(repo.find_t(timestamp))
      result should not be (Some)
      val container = result.get
      container.timestamp.getTime shouldBe (timestamp.getTime +- 1000) //We loose millie second precision
      container.containerNumber should be("HMCU8665487")
      container.containerType should be("54MH")
      container.loaded should be(true)
      container.transportType should be("LARGE")
      container.direction should be("OUT")
      container.identifier should be("ENI 46861")

    }

    "query find" in {
      val timestamp = new Timestamp(System.currentTimeMillis())
      val containersFields = ContainerFields(timestamp, "HMCU8665487", "54MH", true, "LARGE", "OUT", "ENI 46861")
      await(repo.store(containersFields))

      val result = await(repo.find(timestamp, "HMCU8665487"))
      result should not be (Some)
      val container = result.get
      container.timestamp.getTime shouldBe (timestamp.getTime +- 1000) //We loose millie second precision
      container.containerNumber should be("HMCU8665487")
      container.containerType should be("54MH")
      container.loaded should be(true)
      container.transportType should be("LARGE")
      container.direction should be("OUT")
      container.identifier should be("ENI 46861")

    }




}
