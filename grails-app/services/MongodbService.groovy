import org.springframework.beans.factory.InitializingBean
import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.mongodb.ServerAddress
import com.mongodb.MongoOptions


class MongodbService implements InitializingBean {

  boolean transactional = false
  Mongo mongo;
  DB db;
  Boolean enabled = Boolean.FALSE

  void afterPropertiesSet() {
    try {
      String host = ConfigurationHolder.config.mongoDB.host
      int port = ConfigurationHolder.config.mongoDB.port
      String dbName = ConfigurationHolder.config.mongoDB.db
      String dbUsername = ConfigurationHolder.config.mongoDB.userName
      String dbPwd = ConfigurationHolder.config.mongoDB.password
      MongoOptions mopt = new MongoOptions()
      mopt.connectionsPerHost = 20;
      ServerAddress serverAddress = new ServerAddress(host, port)
      mongo = new Mongo(serverAddress, mopt);
      //def check = mongo.getDatabaseNames()
      db = mongo.getDB(dbName);
      //enabled = db.authenticate(dbUsername, dbPwd.toCharArray())
    }
    catch (Exception ex) {
      //log.error("Error connecting to MongoDB", ex)
      enabled = Boolean.FALSE
    }

  }

  def void log(String stopWatchAsString, Throwable exception) {
    //start[1280875264326] time[60] tag[UsersService.checkPersistentLogin]
    if (exception == null && enabled) {
      DBCollection dbCollection = db.getCollection(ConfigurationHolder.config.mongoDB.db)
      BasicDBObject doc = new BasicDBObject()
      doc.put("name", stopWatchAsString)
      dbCollection.insert(doc)
    }
  }

  def void log(String tag, long startTime, long elapsed, String asString, Throwable exception) {
    //start[1280875264326] time[60] tag[UsersService.checkPersistentLogin]
    if (exception == null && enabled) {
      log.info(asString)
      String collectionName = (tag.split("\\."))[0]
      DBCollection dbCollection = db.getCollection(collectionName)
      BasicDBObject doc = new BasicDBObject()
      doc.put("tag", tag)
      doc.put("startTime", startTime)
      doc.put("elapsed", elapsed)
      dbCollection.insert(doc)
    }
  }

}