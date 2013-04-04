package fr.tse.lt2c.satin.IpfhasWorkerShotDetection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Test;
import static org.junit.Assert.*;

import org.json.simple.JSONObject;

/**
 * Test for ShotDetection class
 * @author Antoine Lavignotte
 * @version 1.0
 */
public class ShotDetectionTest {

	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ShotDetectionTest.class);
	
	/**
	 * data to test
	 */
	private JSONObject jsonObj = new JSONObject();
	
	/**
	 * data received
	 */
	private JSONObject dataReceived = new JSONObject();
	
	/**
	 * Test the byte[] conversion into JSONObject
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testConvertDataToJson() {
		logger.info("---- in testConvertDataToJson ----");
		
		jsonObj.put("videoAddress", "http://www.video.fr/toto.mp4");
		jsonObj.put("videoName", "toto.mp4");
		byte[] dataToTest = jsonObj.toJSONString().getBytes();
		
		ShotDetection shotDet = new ShotDetection();
		dataReceived = shotDet.convertDataToJson(dataToTest);
		
		// Test the difference between sended data & received data
		assertEquals(jsonObj, dataReceived);
		
		// Test the name field
		assertEquals(jsonObj.get("videoName"), dataReceived.get("videoName"));
		
		// Test the videoAddress field
		assertEquals(jsonObj.get("videoAddress"), dataReceived.get("videoAddress"));
	}
}
