package controllers

import java.sql.Timestamp

import javax.inject.Inject
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{AbstractController, ControllerComponents, _}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.WriteResult
import repos.{ContainerFields, WidgetRepo, WidgetRepoImpl}

import scala.concurrent.Future



class Widgets @Inject()(val reactiveMongoApi: ReactiveMongoApi) (components: ControllerComponents, repo: WidgetRepo) extends AbstractController(components) {
  private val logger = Logger(this.getClass)


  def index: Action[AnyContent] = Action  {
    Ok("Your store is ready")
  }

  def widgetRepo = new WidgetRepoImpl(reactiveMongoApi)

  import repos.TimeContainerModel._


  def put(): Action[AnyContent] = Action.async { request => {
    println(request)
    try {
      request.body.asJson match {
        case Some(json) =>
          import repos._
          val response: Future[WriteResult] = widgetRepo.store(json.as[ContainerFields])
          response.map(widget => Ok("Record added"))
        case _ => Future {
          Results.UnprocessableEntity("Unable to marshall input")
        }
      }
    }
    catch {
      case e: NoSuchElementException => logger.error(e.getMessage, e); Future(Results.UnprocessableEntity(e.getMessage))
      case e: Throwable => logger.error(e.getMessage, e); Future(Results.InternalServerError(e.getMessage))
    }
  }
  }

  def getContainerByTime(timestamp: Long, containerNumber: String): Action[AnyContent] =
    Action.async {
      import play.api.libs.json._
      import repos.TimeContainerModel._
      widgetRepo.find(new Timestamp(timestamp), containerNumber).map {
        case event: Option[ContainerFields] if event.isDefined => {
          logger.trace("Event is defined");
          Results.Ok(Json.toJson(event.get))
        }
        case _: Option[ContainerFields] => {
          logger.trace("No event available");
          Results.NotFound("No record found")
        }
        case _ => logger.trace(s"Ooops, undefined error"); Results.BadRequest("Error processing request, see log for details")
      }
    }
  def getMovement(timestamp: Long): Action[AnyContent] =
    Action.async {
      import play.api.libs.json._
      import repos.TimeContainerModel._
      widgetRepo.find_t(new Timestamp(timestamp)).map {
        case event: Option[ContainerFields] if event.isDefined => {
          logger.trace("Event is defined");
          Results.Ok(Json.toJson(event.get))
        }
        case _: Option[ContainerFields] => {
          logger.trace("No movement available");
          Results.NotFound("No record found")
        }
        case _ => logger.trace(s"Ooops, undefined error"); Results.BadRequest("Error processing request, see log for details")
      }
    }

}






//  def get_container_time(containerNumber: String, timestamp: String) = Action.async { implicit request =>
//  widgetRepo.find(containerNumber, timestamp).map(Ok(Json.toJson(_)))
  //
  //    widgetRepo.select(BSONDocument("timestamp" -> BSONDocument("$eq" -> timestamp), "containerNumber" -> BSONDocument("$eq" -> containerNumber)))
  //      .m
  // ap(widget => Ok(Json.toJson(widget)))

//
//  def get_timestamp(timestamp: String) = Action.async { implicit request =>
//    widgetRepo.select(BSONDocument("timestamp" -> BSONDocument("$eq" -> timestamp))).map(widget => Ok(Json.toJson(widget)))
//  }

  //val query = BSONDocument("created" -> BSONDocument("$gte" -> timestamp))
  //    BunkerEventCollection.flatMap(
  //      _.find(query)
  //        .sort(Json.obj("created" -> JsNumber(-1)))
  //        .cursor[BunkerEvent]()
  //        .collect[List](-1, Cursor.FailOnError[List[BunkerEvent]]())
  //    )
//  def update(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
//    val timestamp = (request.body \ Timestamp).as[String]
//    val containerNumber = (request.body \ ContainerNumber).as[String]
//    val containerType = (request.body \ ContainerType).as[String]
//    val loaded = (request.body \ Loaded).as[String]
//    val direction = (request.body \ Direction).as[String]
//    val identifier = (request.body \ Identifier).as[String]
//    widgetRepo.update(BSONDocument(id -> BSONObjectID(id)),
//      BSONDocument("$set" -> BSONDocument(Timestamp -> timestamp, ContainerNumber -> containerNumber, ContainerType -> containerType,
//        Loaded -> loaded, Direction -> direction, Identifier -> identifier)))
//      .map(result => Accepted)
//  }

//  def delete(id: String) = Action.async {
//    widgetRepo.remove(BSONDocument(id -> BSONObjectID(id)))
//      .map(result => Accepted)
//  }
//
//}


//object WidgetFields {
//  val timestamp = "Timestamp"
//  val containerNumber = "ContainerNumber"
//  val containType = "ContainerType"
//  val loaded = "Loaded"
//  val direction = "Direction"
//  val identifier = "Identifier"
//}