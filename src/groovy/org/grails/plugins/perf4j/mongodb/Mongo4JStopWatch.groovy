package org.grails.plugins.perf4j.mongodb

import org.perf4j.LoggingStopWatch
import org.codehaus.groovy.grails.commons.ApplicationHolder as AH

/**
 * Created by IntelliJ IDEA.
 * User: alex
 * Date: Aug 3, 2010
 * Time: 1:19:10 PM
 * To change this template use File | Settings | File Templates.
 */
class Mongo4JStopWatch  extends LoggingStopWatch{

  def mongodbService;

  public Mongo4JStopWatch(){
    super()
    mongodbService = AH.application.mainContext.getBean('mongodbService')

  }
  public Mongo4JStopWatch(String tag, String message){
    super(tag, message)
    mongodbService = AH.application.mainContext.getBean('mongodbService')

  }
  protected void log(String stopWatchAsString, Throwable exception) {
    //mongodbService.log(stopWatchAsString, exception);
    String asString = super.toString()
    mongodbService.log(this.getTag(), this.getStartTime(), this.getElapsedTime(), asString , exception)
  }

}
