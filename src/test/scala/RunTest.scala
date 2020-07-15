import com.ksr.dataflow.Job
import com.ksr.dataflow.configuration.job.Configuration
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, FunSuite}

class RunTest extends FlatSpec {

  val path: String = getClass.getResource("/config/sales.yaml").getPath
  val configuration: Configuration = Configuration(path)
  val session = Job(Configuration(path), "test")

  "run" should "should should create a new test session" in {
    assert(session.env === "test")
    assert(session.config.appName.get === "TransactionsApp")
  }

}
