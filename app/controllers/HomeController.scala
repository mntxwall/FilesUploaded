package controllers

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{Files, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import javax.inject._
import models.UserRepository
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc._
import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

case class UserData(files: String)
@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                              userRefineAction: UserRefineAction,
                               userRepo : UserRepository) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  val userForm = Form(
    mapping(
      "zipfiles" -> text
    )(UserData.apply)(UserData.unapply)
  )

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def hello() = Action{

    userRepo.test()
    Ok("hihi")
  }

  def test() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.test())
  }

  def zip() = Action{ implicit request: Request[AnyContent] =>

    val zipFile = "/tmp/test.zip"
    val userData = userForm.bindFromRequest.get

    val buffer = new Array[Byte](1024)

    val fos = new FileOutputStream(zipFile)

    val zos = new ZipOutputStream(fos)


    userData.files.split(",").foreach{x =>


      val srcFile = new File(s"/tmp/fileUploads/$x")

      println(srcFile.getName)

      val fis:FileInputStream = new FileInputStream(srcFile)

      zos.putNextEntry(new ZipEntry(srcFile.getName))

      var length = fis.read(buffer)

      while(length > 0 ) {

        zos.write(buffer, 0, length)
        length = fis.read(buffer)

      }
      zos.closeEntry()

      // close the InputStream

    }

    zos.close()

    //println("userData is "+ userData)


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
