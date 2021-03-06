package com.xebialabs.xltest.generator

import com.xebialabs.xltest.domain.{CompleteProject, PassiveTestSpecification, Project}

import scala.collection.immutable.IndexedSeq

class TestSpecificationGenerator {

  /**
   * Generates a project with one jUnit Test specification
   */
  def generateSimpleTestSet: CompleteProject = {

    val p = Project("Project A")

    val ts = PassiveTestSpecification("jUnit test", Some("xlt.DefaultFunctionalTestsQualifier"), Some("xlt.JUnit"))

    CompleteProject(p, List(ts))
  }


  def generateLargeJunitTestSet(projectName: String, nrOfSpecs: Int): CompleteProject = {
    val p = Project(projectName)

    val specifications: IndexedSeq[PassiveTestSpecification] =
      for (i <- 1 to nrOfSpecs) yield PassiveTestSpecification(f"testspec $i%02d", Some("xlt.DefaultFunctionalTestsQualifier"), Some("xlt.JUnit"))

    CompleteProject(p, specifications)

  }


}
