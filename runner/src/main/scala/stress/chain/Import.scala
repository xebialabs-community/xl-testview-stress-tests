package stress.chain

import java.nio.file.{Files, Path}

import com.typesafe.scalalogging.LazyLogging
import generator.Generator
import stress.config.RunnerConfig
import stress.utils.{ClientUtils, XmlOMatic, FileUtils}
import scala.collection.JavaConverters._

import scala.xml.Node

object Import extends LazyLogging {

  /*
 Somehow scala by default uses another XML handler than Java. Scala uses the IdentityTransformerHandler, while java uses a TransformerHandlerImpl.
 The identityTransformerHandler crashes when using benerator
 Setting this system property overrides the default scala factory by the java one.
  */
  System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl")

  val generator = new Generator
  val clientUtils = new ClientUtils

  val testResultFeeder = Iterator.continually(Map("filename" -> {
    logger.info("Generating another test set")
    val data: List[Path] = generator.createTestData(RunnerConfig.importRuns.filesPerTestSpec, 1).asScala.toList
    val updatedXml: List[List[Node]] = data.map(dir => {
      val xmlFiles: List[Path] = scanDirectories(dir)
      val xmlNodes: List[Node] = loadXmlFiles(xmlFiles)
      updateTimestamps(xmlNodes)
    })

    val listOfZips: List[Path] = updatedXml.map(f => FileUtils.zip(f.map(g => FileUtils.saveXml(g))))

    listOfZips.head.toAbsolutePath.toString
  }))

  def updateTimestamps(xmlNodes: List[Node]): List[Node] = {
    xmlNodes.map(xmlNode => XmlOMatic.updateTime(xmlNode))
  }

  def loadXmlFiles(xmlFiles: List[Path]): List[Node] = {
    val xmlNodes: List[Node] = xmlFiles.map(f => scala.xml.XML.load(Files.newInputStream(f)))
    xmlNodes
  }

  def scanDirectories(dir: Path): List[Path] = {
    val xmlFiles: List[Path] = Files.newDirectoryStream(dir).asScala.toList
    xmlFiles
  }

}
