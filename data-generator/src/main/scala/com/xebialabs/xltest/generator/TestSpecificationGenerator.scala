package com.xebialabs.xltest.generator

import com.xebialabs.xltest.domain.{CompleteProject, PassiveTestSpecification, Project}

class TestSpecificationGenerator {

  /**
   * Generates a project with one jUnit Test specification
   */
  def generateSimpleTestSet: CompleteProject = {

    val p = Project("--", "Project A")

    val ts = PassiveTestSpecification("--", "jUnit test", Some("xlt.DefaultFunctionalTestsQualifier"), "xlt.JUnit")

    CompleteProject(p, List(ts))
  }


}
