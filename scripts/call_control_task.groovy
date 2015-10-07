String XLD_URL = System.getenv('XLD_URL')
String XLD_CONTEXT = new URL(XLD_URL).path
String XLD_CREDENTIALS = System.getenv('XLD_CREDENTIALS')
String XLD_USERNAME = XLD_CREDENTIALS.split(':')[0]
String XLD_PASSWORD = XLD_CREDENTIALS.split(':')[1]
String CONTROL_CI_ID = System.getenv('CONTROL_CI_ID')
String CONTROL_ACTION = System.getenv('CONTROL_ACTION')
int CONTROL_TASK_TIMEOUT = 300


@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.6' )
import static groovyx.net.http.ContentType.*
import groovyx.net.http.RESTClient
import org.apache.http.client.ClientProtocolException

def xld = new RESTClient(XLD_URL)
xld.auth.basic(XLD_USERNAME, XLD_PASSWORD)

def controlXml = xld.get(path: "$XLD_CONTEXT/deployit/control/prepare/$CONTROL_ACTION/$CONTROL_CI_ID",
    contentType: TEXT, headers: [Accept : 'application/xml']).data.text
def taskId = xld.post(path: "$XLD_CONTEXT/deployit/control", body: controlXml,
    contentType: TEXT, headers: ['Content-Type' : XML, Accept : JSON]).data.text

println "Starting control task '$CONTROL_ACTION' of CI [$CONTROL_CI_ID] (task ID: $taskId)"
xld.post(path: "$XLD_CONTEXT/deployit/tasks/v2/$taskId/start")

def tries = CONTROL_TASK_TIMEOUT / 5
while (tries-- > 0) {
  println "Waiting for control task to finish ..."
  Thread.sleep(5000)
  try {
    def state = xld.get(path: "$XLD_CONTEXT/deployit/tasks/v2/$taskId").data.@state.text()
    if (state != "EXECUTING") {
      if (state == "EXECUTED") {
        println "Control task '$CONTROL_ACTION' of CI [$CONTROL_CI_ID] has successfully finished (task ID: $taskId)"

        xld.post(path: "$XLD_CONTEXT/deployit/tasks/v2/$taskId/archive")

        return
      } else {
        throw new RuntimeException("Control task '$CONTROL_ACTION' of CI [$CONTROL_CI_ID] failed with state $state, " +
            "please check XL Deploy log files (task ID: $taskId)")
      }
    }
  } catch (ClientProtocolException e) {
    println "Failed to call XL Deploy, will retry: ${e.getMessage()}"
  }
}

throw new RuntimeException("Control task '$CONTROL_ACTION' of CI [$CONTROL_CI_ID] did not finish in " +
    "${CONTROL_TASK_TIMEOUT} seconds, please check XL Deploy log files (task ID: $taskId)")
