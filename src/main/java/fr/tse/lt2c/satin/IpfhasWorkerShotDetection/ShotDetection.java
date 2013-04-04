package fr.tse.lt2c.satin.IpfhasWorkerShotDetection;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.bson.types.ObjectId;
import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;
import org.openimaj.video.processing.shotdetector.ShotBoundary;
import org.openimaj.video.processing.shotdetector.VideoShotDetector;
import org.openimaj.video.xuggle.XuggleVideo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class called by a worker call names "ShotDetection"
 * @author Antoine Lavignotte
 * @version 1.0
 */
public class ShotDetection extends IpfhasWorkerShotDetection implements GearmanFunction {

	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ShotDetection.class);

	/**
	 * Video id
	 */
	private ObjectId videoId;

	/**
	 * Video name
	 */
	protected String videoName;

	/**
	 * Video Address
	 */
	private String videoAddress;

	/**
	 * Folder Path
	 */
	private String folderPath;

	/**
	 * Video Folder File
	 */
	private File folder;

	/**
	 * Video Path File
	 */
	private File videoPath;

	/**
	 * Video Url
	 */
	private URL videoUrl;
	
	/**
	 * Shots Path File
	 */
	protected File shotPath;
	
	/**
	 * Shot detection threshold
	 */
	private int shotThreshold;

	/**
	 * Constructor
	 */
	public ShotDetection() {	
	}

	/**
	 * Automatically called by the worker
	 * @param function Gearman function name
	 * @param data	Gearman job data
	 * @param callback An object used to send intermediate data back to the client while the job is executing
	 * @return byte[] Result data of the job's execution
	 */
	@Override
	public byte[] work(String function, byte[] data, GearmanFunctionCallback callback)
			throws Exception {
		try {
			logger.info("---- In ShotDetection ----");

			// Convert data to byte[] into JSONObject
			JSONObject dataJson = convertDataToJson(data);

			// Extract data from JSONObject
			videoId = (ObjectId) dataJson.get("_id");

			// Find videoName in the database
			//videoName = dataJson.get("videoName").toString();
			//videoAddress = dataJson.get("videoAddress").toString();
			videoName = super.getMongoConn().findInMongoDb("_id", videoId, "videoName").toString();
			videoAddress = super.getMongoConn().findInMongoDb("_id", videoId, "videoAddress").toString();
			folderPath = dataJson.get("folderPath").toString();
			shotThreshold = (int) dataJson.get("shotThreshold");

			// Initialize variables
			folder = new File(folderPath);

			// Video initialization
			VideoInitialization videoInit = new VideoInitialization();

			// Create video folder
			videoInit.createVideoFolder(folder);

			// Initialize variables
			videoPath = new File(folder.getAbsoluteFile() + "/" + videoName);
			videoUrl = new URL(videoAddress);	

			// Copy video from an url to the folder
			videoInit.copyVideoFromUrl(videoUrl, videoPath);

			// Initialize variable shotPath
			shotPath = new File(videoPath.getAbsolutePath() + "/Shots");
			
			// Create a folder to stock the shot images
			videoInit.createVideoFolder(shotPath);

			// Launch the shot detection
			JSONArray listShots = openImajWork(this.videoPath);
			
			// Insert the shot list into the database
			super.getMongoConn().addInMongoDb("_id", videoId, "listShot", listShots);
			
			// Return the shots list
			return listShots.toJSONString().getBytes();
		}
		catch(Exception e) {
			logger.error("Bug in ShotDetection: {}", e);
			return null;
		}
	}

	/**
	 * Convert a byte[] data into JSONObject data
	 * @param data Data to convert into JSON
	 * @return Data converted into a JSONObject
	 */
	public JSONObject convertDataToJson(byte[] data) {
		try {
			logger.info("---- In convertDataToJson ----");

			String dataString = new String(data);
			Object obj = JSONValue.parse(dataString);
			JSONObject dataJsonObject = (JSONObject) obj;
			return dataJsonObject;
		}
		catch(Exception e) {
			logger.error("Bug in convertDataToJSON: {}", e);
			return null;
		}
	}

	/**
	 * OpenImaj work for shot detection
	 * @param videoPath String of the video Path
	 * @return JSONArray List of the shots
	 */
	private JSONArray openImajWork(File videoPath) {
		try {
			logger.info("---- in openImajWork ----");

			// Video Instantiation
			Video<MBFImage> video = new XuggleVideo(videoPath);
			
			// Launch shot detection
			VideoShotDetector vsd = new VideoShotDetector(video, false);
			
			// Listener instantiation
			Listener listener = new Listener();
			
			// Parameters for the shotDetection
			vsd.setFindKeyframes(true);
			vsd.addShotDetectedListener(listener);
			vsd.setThreshold(shotThreshold);
			vsd.process();
			
			List<ShotBoundary<MBFImage>> listShot = vsd.getShotBoundaries();

			// Debug
			for(int i=0; i<listShot.size(); i++) {
				logger.debug("{}", listShot.get(i).toString());
			}

			// Convert listShot into a JSONArray
			JSONArray listShotJson = listShotToJsonArray(listShot);

			return listShotJson;
		}
		catch(Exception e) {
			logger.error("Bug in openImajWork: {}", e);
			return null;
		}
	}
	
	/**
	 * Convert a list of Shots into a JSONArray
	 * @param list List of Shots detected
	 * @return JSONArray of Shots detected
	 */
	private JSONArray listShotToJsonArray(List<ShotBoundary<MBFImage>> list) {
		try {
			logger.info("---- in listShotToJsonArray ----");
			JSONArray list_json = new JSONArray();
			for(int i=0; i<list.size(); i++) {
				list_json.add(list.get(i).toString());
			}
			logger.debug("ListShotJson : {}", list_json.toJSONString());
			return list_json;
		}
		catch(Exception e) {
			logger.error("Bug in listShotToJsonArray: {}", e);
			return null;
		}
	}
}
