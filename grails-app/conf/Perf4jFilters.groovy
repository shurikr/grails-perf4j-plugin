import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU
import org.perf4j.log4j.Log4JStopWatch
import org.grails.plugins.perf4j.ProfiledOptionsBuilder
import org.apache.log4j.Logger


public class Perf4jFilters {
    
    // the name of the config property in controllers
    static final String PROFILED_PROPERTY = "profiled"
    // the key used to store the stopwatch object in the request
    static final String STOPWATCH_REQUEST_KEY = 'perf4jplugin.stopwatch'
    
    // profiling options for each controller which has a "profiled" property of type Closure (controller name is map key)
    // this is needed to cache the options, so we don't have to execute the closure upon each request
    // TODO: move this somewhere else and update it upon controller changes (otherwise, runtime config changes won't be possible)!
    static profilingOptions = [:].asSynchronized()
    
    static log = Logger.getLogger(Perf4jFilters)

    
    def filters = {
        def log = Logger.getLogger(Perf4jFilters)
        
        all(controller:'*', action: '*') {
            before = {
                if(controllerName) {
                    def action = actionName ?: 'index'
                    
                    def controllerClass = grailsApplication.getArtefactByLogicalPropertyName("Controller", controllerName)
                    if(controllerClass) {
                        def profiled = GCU.getStaticPropertyValue(controllerClass.clazz, PROFILED_PROPERTY)

                        if(profiled instanceof Boolean && profiled) {
                            log.trace "Boolean type profiled property in controller ${controllerName}"
                            createStopwatch(null, null, controllerName, action, request)
                        }
                        else if(profiled instanceof List && profiled.contains(action)) {
                            log.trace "Collection type profiled property in controller ${controllerName}"
                            createStopwatch(null, null, controllerName, action, request)
                        }
                        else if(profiled instanceof Closure) {
                            log.trace "Closure type profiled property in controller ${controllerName}"
                            
                            if(!profilingOptions.containsKey(controllerName)) {
                                log.trace "Evaluating profiled DSL in controller ${controllerName}"
                                
                                // run closure with builder as delegate
                                def builder = new ProfiledOptionsBuilder()
                                profiled.delegate = builder
                                profiled.resolveStrategy = Closure.DELEGATE_ONLY
                                profiled.call()
                                profilingOptions[controllerName] = builder.profiledMap
                            }
                            else {
                                log.trace "Using cached profiling options for controller ${controllerName}"
                            }

                            def options = profilingOptions[controllerName]
                            if(options && options.containsKey(action)) {
                                createStopwatch(options[action].tag, options[action].message, controllerName, action, request)
                            }
                        }
                    }
                }
            }

            
            after = {
                def stopwatch = request[STOPWATCH_REQUEST_KEY]
            
                if(stopwatch) {
                    stopwatch.stop()
                    log.trace "Stop watch stopped: ${stopwatch.tag}"
                }
            }


            afterView = {
            }
        }
    }
    
    
    /**
     *  Create the stop watch and store it in the request, so it can be accessed in the after/afterView interceptors.
     */
    private static createStopwatch(tag, message, controllerName, actionName, request) {
        if(!tag) {
            tag = "${controllerName}.${actionName}"
        }
        
        def stopwatch = new Log4JStopWatch(tag, message)

        request[STOPWATCH_REQUEST_KEY] = stopwatch
        log.trace "Stop watch started: ${tag}"
    }
}