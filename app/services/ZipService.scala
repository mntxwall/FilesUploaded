package services

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.{Files, Path, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.{ZipEntry, ZipOutputStream}

import controllers.UserData
import play.api.data.Form

object ZipService {

  def doFileCompress(filename: String):Unit = {
    val zipFile = "/tmp/test.zip"
    // val userData = userForm.bindFromRequest.get

    val buffer = new Array[Byte](1024)

    val fos = new FileOutputStream(zipFile)

    val zos = new ZipOutputStream(fos)


    val srcFile = new File(filename)

    println(srcFile.getName)

    val fis:FileInputStream = new FileInputStream(srcFile)

    zos.putNextEntry(new ZipEntry(srcFile.getName))

    var length = fis.read(buffer)

    while(length > 0 ) {

      zos.write(buffer, 0, length)
      length = fis.read(buffer)

    }
    zos.closeEntry()
    zos.close()

  }

  def compressFiles(userData: UserData) = {


    /*
    * 添加图片到压缩文件中
    */

    userData.files.split(",").foreach{x =>

      doFileCompress(s"/tmp/fileUploads/$x")

      /*
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
      */

      // close the InputStream

    }

  }

  def compressText(userData: UserData) = {

    /**
      *
      * 添加文字到压缩文件中
      * 首先把html上传的文本文字转变成文本文件
      */


    val newFilePath:Path = Paths.get(s"/tmp/fileUploads/${LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}.txt")
    Files.createFile(newFilePath)

    Files.write(newFilePath, userData.contentText.getBytes)

    //Files.

    //FileSystems.new


    doFileCompress(s"/tmp/fileUploads/${newFilePath.getFileName.toString}")

    /*
    val srcFile = new File(s"/tmp/fileUploads/${newFilePath.getFileName.toString}")

    val fis:FileInputStream = new FileInputStream(srcFile)

    zos.putNextEntry(new ZipEntry(srcFile.getName))

    var length = fis.read(buffer)

    while(length > 0 ) {

      zos.write(buffer, 0, length)
      length = fis.read(buffer)

    }
    zos.closeEntry()
     */
  }


}
