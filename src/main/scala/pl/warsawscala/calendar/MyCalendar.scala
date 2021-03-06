package pl.warsawscala.calendar

import java.time.LocalDate

import com.ning.http.client.AsyncHttpClientConfig
import play.api.libs.ws.DefaultWSClientConfig
import play.api.libs.ws.ning.{NingAsyncHttpClientConfigBuilder, NingWSClient}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait MyCalendar {
  def getEventsFor(from: LocalDate, to: LocalDate): Future[Seq[PlannedEvent]] // ???
}

object MyCalendar {
  //def apply(Code: String): MyCalendar = MyCalendarImp(Code)
  def apply(code: String): MyCalendar = MyCalendarImpl(code)
}

case class MyCalendarImpl(code: String) extends MyCalendar {
  val config = new NingAsyncHttpClientConfigBuilder(DefaultWSClientConfig()).build
  val builder = new AsyncHttpClientConfig.Builder(config)
  val client = new NingWSClient(builder.build)

  def getEventsFor(from: LocalDate, to: LocalDate): Future[Seq[PlannedEvent]] = {

    getAuthToken flatMap {
      getCalendarEntries(_) flatMap { s => s }
    }

    //todo
    //val EventsList = getEventsByDate(Code, from, to)
    //ParseEvents(EventsList)
  }

  def getAuthToken: Future[String] = {
    val postData: Map[String, String] = Map("code" -> code,
      "client_id" -> "468805955202-t2ahu8an02kmvc13pk15adp8to1nitc0.apps.googleusercontent.com",
      "client_secret" -> "YQig_XAAns1PlEdS7XNXqwB6",
      "redirect_uri" -> "http://localhost:9000/oauth2callback",
      "grant_type" -> "authorization_code")

    client.url("https://www.googleapis.com/oauth2/v4/token")
      .withHeaders(("Content-Type", "application/x-www-form-urlencoded"))
      .post(postData) map {
      response => (response.json \ "access_token").as[String]
    }
  }

  def getCalendarEntries(authToken: String) = {
    client.url("https://www.googleapis.com/calendar/v3/users/me/calendarList")
      .withQueryString("access_token" -> authToken)
      .get() map {
      response =>
        println("Calendar API response list: " + response.json.toString())
        MyCalendarStub().getEventsFor(new LocalDate(), new LocalDate())
    }
  }

  def ParseTags(summary: String): List[String] = {
    if (summary.startsWith("#")) {
      summary.split("#").zipWithIndex.filter(_._2 % 2 == 0).map(_._1).toList
    } else {
      summary.split("#").zipWithIndex.filter(_._2 % 2 == 1).map(_._1).toList
    }
  }

  /*  def ParseEvents(EventsList: List[GoogleEvent]): List[PlannedEvent] = {
      EventsList.map(e => PlannedEvent(e.startDate, e.endDate, ParseTags(e.summary)))
    }

  def ParseTags(Summary: String): List[String] = {
    //Parsowanie Tagów
  }
  */
}

case class MyCalendarStub() extends MyCalendar {
  def getEventsFor(from: LocalDate, to: LocalDate): Future[Seq[PlannedEvent]] = {
    Future {
      val myPlannedEvent: PlannedEvent = PlannedEvent(new LocalDate(2016, 3, 1), new LocalDate(2017, 3, 10), List("holiday", "icecreamdays"))
      val myPlannedEvent2: PlannedEvent = PlannedEvent(new LocalDate(2016, 5, 5), new LocalDate(2016, 5, 10), List("grilldays"))
      val myPlannedEvent3: PlannedEvent = PlannedEvent(new LocalDate(2016, 7, 1), new LocalDate(2016, 7, 5), List("holiday"))
      List(myPlannedEvent, myPlannedEvent2, myPlannedEvent3)
    }
  }
}

case class PlannedEvent(startDate: LocalDate, endDateExclusive: LocalDate, tags: Seq[String])

// ???

