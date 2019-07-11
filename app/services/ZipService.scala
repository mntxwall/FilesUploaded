package services

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.{Files, Path, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.{ZipEntry, ZipOutputStream}

import controllers.UserData
import play.api.data.Form

object ZipService  {


  def doFileCompress(filename: String, zos: ZipOutputStream):Unit = {

    val buffer = new Array[Byte](1024)
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
  }

  def compressFiles(userData: UserData, zipFile: String, uploadPath:String):Unit = {

    /**
      *
      * 添加文字到压缩文件中
      * 首先把上传的图片放入压缩包中，再把文字形成文档，放入压缩包中
      * 再把文本文档放入压缩包中
      */

    val fos = new FileOutputStream(zipFile)
    val zos = new ZipOutputStream(fos)
    /*
    * 进行图片的压缩
    * */
    userData.files.split(",").foreach{x =>

      doFileCompress(s"$uploadPath/$x", zos)
    }


    val newFilePath:Path = Paths.get(s"$uploadPath/${LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}.txt")
    Files.createFile(newFilePath)

    Files.write(newFilePath, userData.contentText.getBytes)
    doFileCompress(s"$uploadPath/${newFilePath.getFileName.toString}", zos)

    zos.close()
  }

  def compressText(userData: UserData, zipFile: String):Unit = {

    /**
      *
      * 添加文字到压缩文件中
      * 首先把上传的图片放入压缩包中，再把文字形成文档，放入压缩包中
      * 再把文本文档放入压缩包中
      */

    val fos = new FileOutputStream(zipFile)

    val zos = new ZipOutputStream(fos)

    val newFilePath:Path = Paths.get(s"/tmp/fileUploads/${LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))}.txt")
    Files.createFile(newFilePath)

    Files.write(newFilePath, userData.contentText.getBytes)

    doFileCompress(s"/tmp/fileUploads/${newFilePath.getFileName.toString}", zos)

    zos.close()

  }


}
