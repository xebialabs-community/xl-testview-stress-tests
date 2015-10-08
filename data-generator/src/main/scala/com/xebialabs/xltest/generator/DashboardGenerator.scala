package com.xebialabs.xltest.generator

import com.xebialabs.xltest.domain.Dashboard

object DashboardGenerator {

  def getEmptyDashboard(): Dashboard = {
    Dashboard("My dashboard")
  }

}
