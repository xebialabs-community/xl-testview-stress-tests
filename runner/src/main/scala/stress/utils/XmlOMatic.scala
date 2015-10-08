package stress.utils

import org.joda.time.LocalDateTime
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}

import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Elem, Node, _}

object XmlOMatic {
  private val JUNIT_TIME_FORMAT: DateTimeFormatter = DateTimeFormat.forPattern("yyy-MM-dd'T'HH:mm:ss")

  val rule1 = new RewriteRule {
    override def transform(n: Node) = n match {
      case e @ <testsuite>{_*}</testsuite> => e.asInstanceOf[Elem] %
        Attribute(null, "timestamp", LocalDateTime.now().toString(JUNIT_TIME_FORMAT), Null)
      case _ => n
    }
  }

  private val transformer: RuleTransformer = new RuleTransformer(rule1)

  def updateTime(xml:Node): Node = {
    transformer.transform(xml).head
  }
}

