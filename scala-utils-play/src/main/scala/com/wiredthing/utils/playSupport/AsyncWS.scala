package com.wiredthing.utils.playSupport

import java.io.ByteArrayInputStream

import com.ning.http.client.{AsyncCompletionHandler, Response, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise

/**
 * Example taken from http://stackoverflow.com/questions/25432230/it-seems-that-it-is-not-possible-to-do-post-with-body-10k-with-scala-play-ws-fr
 */
object AsyncWS {
  def main(args: Array[String]): Unit = {
    val config = new AsyncHttpClientConfig.Builder()
    val client = new AsyncHttpClient(config.build())

    // this will send back your request including the body
    val url = "http://httpbin.org/post"

    // testing the basic approach by still reading from a string -- this
    // should work if you can fir your text into a string, otherwise
    // you'll need to write some stream magic
    val testInputStream =
      new ByteArrayInputStream("s short test string".getBytes)

    val requestBuilder = client.preparePost(url)
    requestBuilder.setBody(testInputStream)

    val request = requestBuilder.build()
    var result = Promise[Int]()
    client.executeRequest(request, new AsyncCompletionHandler[Response]() {
      override def onCompleted(response: Response) = {
        println("got an answer")
        val is = response.getResponseBodyAsStream()
        val in = scala.io.Source.fromInputStream(is)
        in.getLines().foreach(println(_))
        in.close()
        result.success(response.getStatusCode())
        response
      }

      override def onThrowable(t: Throwable) {
        result.failure(t)
      }
    })
    val getFuture = result.future

    getFuture.map { status =>
      println("result status = " + status)
    }.recover {
      case e: Throwable => {
        println("error e = " + e)
      }
    }
  }
}