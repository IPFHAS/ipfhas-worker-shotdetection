package fr.tse.lt2c.satin.IpfhasWorkerShotDetection;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.openimaj.image.MBFImage;
import org.openimaj.video.Video;
import org.openimaj.video.processing.shotdetector.HistogramVideoShotDetector;
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
	 * Video extension
	 */
	private String videoExtension;

	/**
	 * Video Address
	 */
	private String videoAddress;

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
	 * Video duration
	 */
	private Long videoDuration;

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

			logger.debug("data received: {}", dataJson);
			
			// Extract data from JSONObject
			videoId = (ObjectId) dataJson.get("_id");
			videoName = dataJson.get("videoName").toString();
			videoAddress = dataJson.get("videoAddress").toString();
			shotThreshold = Integer.parseInt(dataJson.get("shotThreshold").toString());
			videoExtension = dataJson.get("videoExtension").toString();

			// Initialize variables
			folder = new File(folderPath);

			// Initialize variables
			videoPath = new File(folder.getAbsoluteFile() + "/" + videoName + "/" + videoName + '.' + videoExtension);	

			// Launch the shot detection
			List<ShotBoundary<MBFImage>> listShotsBoundary = openImajWork(this.videoPath);
			JSONArray listShots = new JSONArray();
			
			for(int i=0; i<listShotsBoundary.size(); i++) {
				listShots.add(listShotsBoundary.get(i).toString());
			}
			
			//Prepare data to send
			JSONObject sendBack = new JSONObject();
			sendBack.put("listShots", listShots);
			sendBack.put("videoDuration", videoDuration);
			
			logger.debug("sendBack: {}", sendBack.toJSONString());
			
			// Return the shots list
			return sendBack.toJSONString().getBytes();
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
			
			logger.debug("dataJsonObject: {}", dataJsonObject);
			
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
	 * @return JSONObject List of the shots
	 */
	private List<ShotBoundary<MBFImage>> openImajWork(File videoPath) {
		try {
			logger.info("---- in openImajWork ----");

			XuggleVideo video;
			
			// Video Instantiation
			if(videoAddress.startsWith("http")){
				videoUrl = new URL(videoAddress);
				video = new XuggleVideo(videoUrl);
			}
			else {
				video = new XuggleVideo(videoAddress);
			}
			
			// Find the video duration
			videoDuration = video.getDuration();
			
			// Launch shot detection
			HistogramVideoShotDetector vsd = new HistogramVideoShotDetector(video, false);
			
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
			
			return listShot;
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
