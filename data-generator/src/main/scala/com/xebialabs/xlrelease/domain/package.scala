package com.xebialabs.xlrelease

import org.threeten.bp.{LocalDateTime, ZoneId, ZonedDateTime}

import scala.language.implicitConversions

package object domain {

  trait Ci {
    def id: String

    def `type`: String
  }

  trait PlanItem extends Ci {
    def title: String

    def status: String
  }

  trait BaseTestSpecification extends Ci {
    def id: String

    def title: String

    def qualificationType: String
  }

  case class Project(id: String,
                     title: String,
                     `type`: String = "xlt.Project") extends Ci

  case class PassiveTestSpecification(id: String,
                                      title: String,
                                      qualificationType: String,
                                      testToolName: String,
                                      `type`: String = "xlt.PassiveTestSpecification") extends BaseTestSpecification

  case class ActiveTestSpecification(id: String,
                                     title: String,
                                     qualificationType: String,
                                     testToolName: String,
                                     searchPattern: String,
                                     workingDirectory: String,
                                     host: String,
                                     `type`: String = "xlt.PassiveTestSpecification") extends BaseTestSpecification

  case class ExecutableTestSpecification(id: String,
                                         title: String,
                                         qualificationType: String,
                                         testToolName: String,
                                         searchPattern: String,
                                         workingDirectory: String,
                                         host: String,
                                         commandLine: String,
                                         timeOut: Int,
                                         scriptLocation: String,
                                         `type`: String = "xlt.PassiveTestSpecification") extends BaseTestSpecification
}
