package com.pagerduty.arrivals.example

import akka.NotUsed
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.pagerduty.arrivals.aggregator.OneStepAggregator

object ExampleAggregator extends OneStepAggregator[UserId, String, AddressingConfig] {
  import ExampleUpstream._

  override def handleIncomingRequest(incomingRequest: HttpRequest, authData: UserId): HandlerResult = {

    val requests = Map(
      "cats" -> (CatsUpstream, HttpRequest(uri = "/cats")),
      "dogs" -> (DogsUpstream, HttpRequest(uri = "/dogs"))
    )
    Right((NotUsed, requests))
  }

  override def buildOutgoingResponseStateless(upstreamResponses: ResponseMap): HttpResponse = {
    val (_, catBody) = upstreamResponses("cats")
    val (_, dogBody) = upstreamResponses("dogs")

    HttpResponse().withEntity(catBody + "\n" + dogBody)
  }
}
