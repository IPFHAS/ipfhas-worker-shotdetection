package fr.tse.lt2c.satin.IpfhasWorkerShotDetection;

import java.io.File;
import java.net.URL;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for VideoInitialization class
 * @author Antoine Lavignotte
 * @version 1.0
 */
public class VideoInitializationTest {

	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(VideoInitializationTest.class);
	
	/**
	 * Video Name
	 */
	private String videoName = "keyboardcat.flv";
	
	/**
	 * Video Path
	 */
	private String videoAddress = "http://dl.dropbox.com/u/8705593/keyboardcat.flv";
	
	/**
	 * Folder Path
	 */
	private String folderPath = "/usr/local/Video/Keyboardcat";
	
	/**
	 * File folder path
	 */
	private File folder = new File(folderPath);
	
	/**
	 * File video path
	 */
	private File videoPath = new File(folder.getAbsoluteFile() + "/" + videoName);
	
	/**
	 * Video url
	 */
	private URL videoUrl =  null;	
	
	/**
	 * Test the creation of a video folder
	 */
	@Test
	public void testCreateVideoFolder() { 
		VideoInitialization videoInit = new VideoInitialization();
		// Test the folder creation
		assertTrue(videoInit.createVideoFolder(folder));
	}
	
	/**
	 * Test the video download from internet
	 */
	@Test
	public void testCopyVideoFromUrl() {
		try {
			VideoInitialization videoInit = new VideoInitialization();
			// Initialize videoUrl
			videoUrl = new URL(videoAddress);
			// Test the video download
			assertTrue(videoInit.copyVideoFromUrl(videoUrl, videoPath));
		}
		catch(Exception e) {
			logger.error("Bug in testCopyVideoFromUrl: {}", e);
		}	
	}
}
