package controllers

import java.io.{File, FileInputStream, FileOutputStream, FileWriter}
import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{FileSystem, FileSystems, Files, Path, Paths, StandardCopyOption}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util

import javax.inject._
import models.UserRepository
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc._
import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry
import java.net.URI

import services.ZipService

import scala.collection.mutable

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

case class UserData(files: String, contentText: String)
@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                              userRefineAction: UserRefineAction,
                               userRepo : UserRepository,
                               config: Configuration) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  val userForm = Form(
    mapping(
      "zipfiles" -> text,
      "ziptext" ->text
    )(UserData.apply)(UserData.unapply)
  )

  def index() = userRefineAction { implicit request: Request[AnyContent] =>

    Ok(views.html.files())
  }

  def hello() = Action{

    //userRepo.test()
    //Ok("hihi")
    Ok(views.html.files())
  }


  def files() = userRefineAction{

    Ok(views.html.files())
  }

  def test() = Action { implicit request: Request[AnyContent] =>
    
    Ok(views.html.files())
  }

  def zip() = Action{ implicit request: Request[AnyContent] =>


    val userData = userForm.bindFromRequest.get

    println(userData)

    val zipFileName:String = s"${config.get[String]("wei.filepath.zip")}/${LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}.zip"
    val fileUploadPath: String = config.get[String]("wei.filepath.upload")

    println(zipFileName)

    ZipService.compressFiles(userData, zipFileName, fileUploadPath)

    Ok("Hello")
  }

  def upload() = Action(parse.multipartFormData){ implicit request =>

    /*
    * upload the file to /tmp directory
    * rename file name to miniseconds
    * */
    request.body.file("file").map{ x =>

      println(x.filename)
      //val filename = LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
      val filename = s"${LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}.${x.filename.split("\\.")(1)}"
      //val filename = x.filename
      val fileWithPath:String = s"/tmp/fileUploads/$filename"
      //val fileWithPath:String = s"/tmp/fileUploads/$filename"

      x.ref.atomicMoveFileWithFallback(Paths.get(fileWithPath))

      Files.setPosixFilePermissions(Paths.get(fileWithPath),
        PosixFilePermissions.fromString("rw-r--r--"))

      val json:JsValue = Json.obj("file" -> s"$filename", "upresult" -> 0)

      //Ok(Json.parse(s"""{"upresult":0 }"""))
      Ok(json)

    }.getOrElse(Ok(Json.parse(s"""{"upresult":1 }""")))

  }

}
