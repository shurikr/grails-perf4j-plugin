package org.grails.plugins.perf4j

import org.perf4j.LoggingStopWatch
import org.grails.plugins.perf4j.mongodb.Mongo4JStopWatch
import org.perf4j.log4j.Log4JStopWatch

/**
 * Created by IntelliJ IDEA.
 * User: alex
 * Date: Aug 3, 2010
 * Time: 7:49:59 PM
 * To change this template use File | Settings | File Templates.
 */
class Perf4jLoggerFactory {
  public static LoggingStopWatch create(String type, String tag, String message){
    switch(type){
      case "mongo" :
        return (tag != null || message != null)?  new Mongo4JStopWatch(tag, message): new Mongo4JStopWatch()
        break
      case "log4j":
      return (tag != null || message != null)?  new Log4JStopWatch(tag, message): new Log4JStopWatch()
        break;
      default:
      return (tag != null || message != null)?  new Log4JStopWatch(tag, message): new Log4JStopWatch()
        break
    }
  }
}
