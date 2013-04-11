package fr.tse.lt2c.satin.IpfhasWorkerShotDetection;

import java.io.File;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.video.processing.shotdetector.ShotBoundary;
import org.openimaj.video.processing.shotdetector.ShotDetectedListener;
import org.openimaj.video.processing.shotdetector.VideoKeyframe;
import org.openimaj.video.timecode.VideoTimecode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listener extends ShotDetection implements ShotDetectedListener<MBFImage> {

	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Listener.class);
	
	
	/**
	 * Not used for now
	 */
	@Override
	public void differentialCalculated(VideoTimecode arg0, double arg1,
			MBFImage arg2) {
		// Not used for now
		
	}

	/**
	 * Automatically called by ShotDetectionWorker when a shot is detected
	 * Make a copy of the image detected as a shot
	 * @param ShotDetected
	 */
	@Override
	public void shotDetected(ShotBoundary<MBFImage> sb,
			VideoKeyframe<MBFImage> vk) {
		
		try {
			// Not used for now
			/*
			File outputFile = new File(shotPath.getAbsolutePath() 
					+ "/" + videoName 
					+ "_" + sb.toString()
					+ ".png");
			
			ImageUtilities.write(vk.getImage(), "png", outputFile);
			*/
		}
		catch(Exception e) {
			logger.error("Bug in shotDetected: {}", e);
		}
		
	}

}
