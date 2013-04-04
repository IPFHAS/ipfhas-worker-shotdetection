package fr.tse.lt2c.satin.IpfhasWorkerShotDetection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;

import static org.junit.Assert.*;

/**
 * Test for MongoDbConnection class
 * @author Antoine Lavignotte
 * @version 1.0
 */
public class MongoDbConnectionTest {

	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(MongoDbConnection.class);

	/**
	 * Mongo address
	 */
	private String mongoAddress = "localhost";

	/**
	 * Mongo database
	 */
	private String mongoDatabaseName = "test";

	/**
	 * Mongo collection
	 */
	private String mongoCollectionName = "movies";
	
	/**
	 * MongoDbConnection instanciation
	 */
	private MongoDbConnection mongoConn;
	
	/**
	 * Setup: called before each method
	 * (Not used for now)
	 */
	@Before
	public void setup() {
		mongoConn = new MongoDbConnection(
				mongoAddress, 
				mongoDatabaseName, 
				mongoCollectionName);
		BasicDBObject obj = new BasicDBObject("videoName", "Toto");
		obj.put("nbShots", 12);
		mongoConn.getMongoCollection().insert(obj);
	}

	/**
	 * Teardown: executed after each test
	 * (Not used for now)
	 */
	@After
	public void tearDown() {
		mongoConn.getMongoDb().dropDatabase();
	}
	
	/**
	 * Test an insertion to the database and a find request
	 */
	@Test
	public void testAddInMongoDb() {
		try {
			logger.info("---- in testAddInMongoDb ----");
			assertTrue(mongoConn.addInMongoDb(
					"videoName",
					"Toto",
					"videoAddress",
					"http://www.video.fr/toto.mp4"));
		}
		catch(Exception e) {
			logger.error("Bug in testAddInMongoDb: {}", e);
		}		
	}
	
	/**
	 * Test a find in the database
	 */
	@Test
	public void testFindInMongoDb() {
		try {
			logger.info("---- in testFindInMongoDb ----");
			assertEquals(12, mongoConn.findInMongoDb("videoName", "Toto", "nbShots"));
		}
		catch(Exception e) {
			logger.error("Bug in testFindInMongoDb: {}", e);
		}
	}

}
