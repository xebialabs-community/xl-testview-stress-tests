package com.xebialabs.xltest

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigFactory.parseResources
import com.typesafe.scalalogging.LazyLogging
import com.xebialabs.xltest.client.XltClient
import com.xebialabs.xltest.generator.TestSpecificationGenerator

object Main extends App with LazyLogging {

  val config = parseResources("data-generator.conf")
    .withFallback(ConfigFactory.load())

  private val completedReleasesAmount = config.getInt("xl.data-generator.completed-releases")
  private val activeReleasesAmount = config.getInt("xl.data-generator.active-releases")
  private val templatesAmount: Int = config.getInt("xl.data-generator.templates")

  logger.info("Active releases: {}", activeReleasesAmount.toString)
  logger.info("Completed releases: {}", completedReleasesAmount.toString)
  logger.info("Templates: {}", templatesAmount.toString)

  val client = new XltClient(
    config.getString("xl.data-generator.baseUrl"),
    config.getString("xl.data-generator.username"),
    config.getString("xl.data-generator.password"))

//  val importTemplateFuture = client.importTemplate("/many-automated-tasks.xlr")
//
//  val specialDaysFuture = client.createCis(SpecialDayGenerator.generateSpecialDays())
//
  val releaseGenerator = new TestSpecificationGenerator()

//  val dependantReleaseFuture = client.createCis(releaseGenerator.generateDependentRelease())
//  val allReleasesFuture = dependantReleaseFuture.flatMap(_ => {
//    // Creating some content to increase repository size
//    val createTemplateReleasesFutures = releaseGenerator
//      .generateTemplateReleases(templatesAmount)
//      .map(client.createCis)
//    val createActiveReleasesFutures = releaseGenerator
//      .generateActiveReleases(activeReleasesAmount)
//      .map(client.createCis)
//    val createCompletedReleasesFutures = releaseGenerator
//      .generateCompletedReleases(completedReleasesAmount)
//      .map(client.createCis)
//
//    Future.sequence(
//      createTemplateReleasesFutures ++
//         createActiveReleasesFutures ++
//         createCompletedReleasesFutures)
//  })
//
//  val allResponses =Future.sequence(
//    Seq(importTemplateFuture, allReleasesFuture, specialDaysFuture))
//
//  allResponses.andThen {
//    case Failure(ex) =>
//      logger.error("Could not generate data set: ", ex)
//  } andThen {
//    case _ =>
      logger.debug("Shutting down the actor system after everything has been done.")
      client.system.shutdown()
      client.system.awaitTermination()
//  }
}
