import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://api-ecomm.gatling.io")
    .acceptHeader("application/json")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")

  val users = csv("users.csv").circular

  val scn = scenario("Browse and login")
    .exec(
      http("HomePage")
        .get("https://ecomm.gatling.io")
        .check(status.in(200, 304))
    )
    .pause(1, 2)
    .feed(users)
    .exec(
      http("Login")
        .post("/login")
        .asFormUrlEncoded
        .formParam("username", "#{username}")
        .formParam("password", "#{password}")
        .check(status.is(200))
    )

  setUp(
    scn.inject(
      rampUsers(10000).during(30.seconds),
      constantUsersPerSec(100.0 / 30).during(2.minutes)
    )
  ).protocols(httpProtocol)
   .assertions(
     global.responseTime.percentile3.lt(800),
     global.successfulRequests.percent.gt(99)
   )
}