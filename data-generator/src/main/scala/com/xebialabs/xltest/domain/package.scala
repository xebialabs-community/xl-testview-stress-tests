package com.xebialabs.xltest

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

  case class CompleteProject(project: Project,
                             testSpecifications: Seq[BaseTestSpecification])

  case class Project(id: String = "--",
                     title: String,
                     name: Option[String] = None,
                     `type`: String = "xlt.Project") extends Ci

  case class PassiveTestSpecification(override val id: String,
                                      override val title: String,
                                      override val qualificationType: String,
                                      testToolName: String,
                                      `type`: String = "xlt.PassiveTestSpecification") extends BaseTestSpecification

  case class ActiveTestSpecification(override val id: String,
                                     override val title: String,
                                     override val qualificationType: String,
                                     testToolName: String,
                                     searchPattern: String,
                                     workingDirectory: String,
                                     host: String,
                                     `type`: String = "xlt.PassiveTestSpecification") extends BaseTestSpecification

  case class ExecutableTestSpecification(override val id: String,
                                         override val title: String,
                                         override val qualificationType: String,
                                         testToolName: String,
                                         searchPattern: String,
                                         workingDirectory: String,
                                         host: String,
                                         commandLine: String,
                                         timeOut: Int,
                                         scriptLocation: String,
                                         `type`: String = "xlt.PassiveTestSpecification") extends BaseTestSpecification

  case class Dashboard(title: String,
                       name: Option[String] = None,
                       dashboardTiles: Seq[DashboardTile] = List.empty,
                       id: String = "---",
                       autoReloadInterval: Int = 60,
                       `type`: String = "xlt.Dashboard") extends Ci

  case class DashboardTile(id: String = "---",
                           x: Int,
                           y: Int,
                           w: Int,
                           h: Int,
                           reportIcon: String,
                           title: String,
                           testSpecification: String,
                           reportType: String,
                           reportName: String,
                           `type`: String = "xlt.DashboardTile",
                           showCaseDate: Boolean = false) extends Ci

}
