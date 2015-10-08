package stress.utils

import java.io.BufferedInputStream
import java.nio.file.{Files, Path}
import java.util.zip.{ZipEntry, ZipOutputStream}

import scala.xml.{Node, Elem}

object FileUtils {

  def zip(files: Iterable[Path]): Path = {

    val file: Path = Files.createTempFile("import-package", ".zip")
    val zip = new ZipOutputStream(Files.newOutputStream(file))

    files.foreach { path =>
      zip.putNextEntry(new ZipEntry(path.getFileName.toString))
      val in = new BufferedInputStream(Files.newInputStream(path))
      var b = in.read()
      while (b > -1) {
        zip.write(b)
        b = in.read()
      }
      in.close()
      zip.closeEntry()
    }
    zip.close()

    file
  }

  def saveXml(xml: Node): Path = {
    val file = Files.createTempFile("test-result", ".xml")
    scala.xml.XML.save(file.toString, xml, "UTF-8")

    file
  }

}
