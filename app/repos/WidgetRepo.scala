package repos
import java.sql.Timestamp
import java.text.SimpleDateFormat

import javax.inject.Inject
import play.api.Logger
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import reactivemongo.api.commands.WriteResult
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

case class ContainerFields(timestamp: Timestamp, containerNumber: String, containerType: String, loaded: Boolean, transportType: String, direction: String, identifier: String)

trait WidgetRepo {
  def store(movementFields: ContainerFields)(implicit ec: ExecutionContext): Future[WriteResult]
  def find(timestamp: Timestamp, containerNumber: String): Future[Option[ContainerFields]]
  def find_t(timestamp: Timestamp): Future[Option[ContainerFields]]

}


object TimeContainerModel {

  import play.api.libs.json._


  implicit object timestampFormat extends Format[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    def reads(json: JsValue): JsSuccess[Timestamp] = {
      val str = json.as[String]
      JsSuccess(Try(new Timestamp(format.parse(str).getTime)).getOrElse(new Timestamp(0)))
    }

    def writes(ts: Timestamp) = JsString(format.format(ts))
  }

  implicit val TimeContainerFormat: OFormat[ContainerFields] = Json.format[ContainerFields]


}

class WidgetRepoImpl @Inject() (reactiveMongoApi: ReactiveMongoApi)(implicit ec: ExecutionContext) extends WidgetRepo {
  import play.api.libs.json._
  import TimeContainerModel._

  private val logger = Logger(this.getClass)
  implicit lazy val ContainerCollection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection[JSONCollection]("ContainerRepo"))

  def store(containerFields: ContainerFields)(implicit ec: ExecutionContext): Future[WriteResult] = ContainerCollection.flatMap(_.insert(containerFields))

  override def find(timestamp: Timestamp, containerNumber: String): Future[Option[ContainerFields]] = {

    val jsObject = Json.obj("timestamp" -> timestampFormat.writes(timestamp), "containerNumber" -> JsString(containerNumber))
    ContainerCollection.flatMap(
      _.find(jsObject).requireOne[ContainerFields]
        .map(Option[ContainerFields])
        .recover {
          case _ =>
            logger.warn(s"Unable to get movement with timestamp or container number: ")
            None
        })
  }

  override def find_t(timestamp: Timestamp): Future[Option[ContainerFields]] = {

    val jsObject = Json.obj("timestamp" -> timestampFormat.writes(timestamp))
    ContainerCollection.flatMap(
      _.find(jsObject).requireOne[ContainerFields]
        .map(Option[ContainerFields])
        .recover {
          case _ =>
            logger.warn(s"Unable to get movement with timestamp: ")
            None
        })
  }
}




