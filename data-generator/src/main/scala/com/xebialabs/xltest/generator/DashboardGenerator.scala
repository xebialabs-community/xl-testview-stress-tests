package com.xebialabs.xltest.generator

import com.xebialabs.xltest.domain.Dashboard
import spray.http.DateTime

object DashboardGenerator {

  def getEmptyDashboard(): Dashboard = {
    Dashboard("My dashboard")
  }

  def getEmptyDashboards(nr: Int, uniqueValue: String = DateTime.now.toIsoDateTimeString): Seq[Dashboard] = {
    for (i <- 1 to nr) yield Dashboard(s"DB-$uniqueValue-$i")
  }
}
