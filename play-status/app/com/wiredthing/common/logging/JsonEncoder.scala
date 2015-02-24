package com.wiredthing.common.logging

import java.net.InetAddress

import ch.qos.logback.classic.spi.{ThrowableProxyUtil, ILoggingEvent}
import ch.qos.logback.core.encoder.EncoderBase
import com.fasterxml.jackson.core.JsonGenerator.Feature
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.commons.io.IOUtils._
import play.api.Play

class JsonEncoder extends EncoderBase[ILoggingEvent] {

  private val mapper = new ObjectMapper().configure(Feature.ESCAPE_NON_ASCII, true)

  private val dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSSZZ")

  lazy private val appName = Play.current.configuration.getString("appName").getOrElse("APP NAME NOT SET")

  override def doEncode(event: ILoggingEvent) {
    val eventNode = mapper.createObjectNode

    eventNode.put("app", appName)
    eventNode.put("hostname", InetAddress.getLocalHost.getHostName)
    eventNode.put("timestamp", dateFormat.format(event.getTimeStamp))
    eventNode.put("message", event.getFormattedMessage)

    Option(event.getThrowableProxy).map(p =>
      eventNode.put("exception", ThrowableProxyUtil.asString(p))
    )

    eventNode.put("logger", event.getLoggerName)
    eventNode.put("thread", event.getThreadName)
    eventNode.put("level", event.getLevel.toString)

    write(mapper.writeValueAsBytes(eventNode), outputStream)
    write(LINE_SEPARATOR, outputStream)

    outputStream.flush
  }

  override def close() {
    write(LINE_SEPARATOR, outputStream)
  }
}