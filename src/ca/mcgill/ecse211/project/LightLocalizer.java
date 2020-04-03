package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import lejos.robotics.SampleProvider;
import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.DriveUtil.*;

/**
 * This class is used to define functions to perform localization
 * using a light sensor
 * 
 * @author Ricky
 *
 */
public class LightLocalizer {
  
  /**
   * Angular speed of base when rotating
   */
  private static final double BASE_SPEED = WHEEL_RAD * ROTATE_SPEED / (BASE_WIDTH / 2);
  
  /**
   * Sampling interval during sweep (ms)
   */
  private static final int SAMPLING_PERIOD = 40;
  
  /**
   * Amount of samples to be taken during sweep
   */
  private static final int SAMPLES = (int) (1000 * 360.0 / (SAMPLING_PERIOD*BASE_SPEED));
  
  /**
   * Color sampler for red light intensity
   */
  private static SampleProvider colorSample = lightSensor.getRedMode();
  
  /**
   * Amount of angles to skip after detecting a min
   */
  private static double angleSkip = 5.0;
  
  /**
   * Buffer (array) to store color sensor samples. Declared as an instance variable to 
   * avoid creating a new array each time {@code readClSample()} is called.
   */
  private static float[] clData = new float[lightSensor.sampleSize()];
  
  /**
   * Performs localization using calculations from the localization
   * tutorial 
   */
  public void doLocalization() {
    float[] angles = getAngles();
    float thetaX = angles[2] - angles[0];
    float thetaY = 360 - (angles[3] - angles[1]);
    turnBy(angles[0] + thetaX/2 - 180, false); // turn to 0 degree point
    odometer.setTheta(0);
    
    //determine error in y direction (assuming we are in 4th quadrant
    float dy = (float) (CL_DIST*Math.cos(thetaX/2*PI/180));
    moveStraightFor(dy/TILE_SIZE);
    
    //determine error in x direction (assuming we are in 4th quadrant)
    float dx = (float) (CL_DIST*Math.cos(thetaY/2*PI/180));
    turnBy(-90,false); // turn to "-x direction"
    moveStraightFor(dx/TILE_SIZE);
    
    double[] Xyt = odometer.getXyt();
    odometer.setXyt(Math.round(Xyt[0]), Math.round(Xyt[1]), -90);
  }
  
  /**
   * Detects angular location of lines.
   * 
   * Takes samples at specified intervals while turning, calls lineDetection function,
   * and verifies that only 4 lines were detected.
   * 
   * @return array containing angles of lines
   */
  private static float[] getAngles() {
    long updateStart;
    long updateDuration;
    
    /**
     * Array to store light sensor values
     */
    float[] sampleReadings = new float[SAMPLES];
    
    /**
     * Array to store sample readings post difference filter
     */
    float[] angles;
    
    //direct robot so sensor is in 3rd quadrant
    navigator.turnTo(45);
    
    // begin turning
    turnBy(360, true);
    
    // while turning, sample at specified time interval
    for (int i = 0; i != SAMPLES; i++) {
      updateStart = System.currentTimeMillis();
      
      sampleReadings[i] = readClValue();
      
      updateDuration = System.currentTimeMillis() - updateStart;
      sleepFor(SAMPLING_PERIOD - updateDuration); // ensure sampling interval is accurate
    }
    
    angles = lineDetection(sampleReadings);
    
    while (angles[0] == 0 || angles[1] == 0 || angles[2] == 0 || angles[3] == 0 || angles[4] != 0) {
      angles = getAngles();
    }
    // verify that we detected exactly 4 lines, do it again if we did not
    
    return angles;
    
  }
  
  /**
   * Returns the value read by the color sensor
   * 
   * @return the value read by the color sensor
   */
  private static float readClValue() {
    colorSample.fetchSample(clData, 0);
    // extract from buffer
    return clData[0];
  }
  
  /**
   * Applies a difference filter to a circular input array
   * 
   * @param data array of data to apply difference filter to.
   * @return filtered data array
   */
  private static float[] differenceFilter(float[] data) {
    float[] output = new float[data.length];
    
    output[0] = data[0] - data[data.length - 1]; // assuming circular data
    
    for (int i = 1; i != data.length; i++) {
      output[i] = data[i] - data[i-1];
    }
    
    return output;
  }
  
  /**
   * Discretizes array to Ternary (-1, 0, 1)
   * 
   * @param data array of data to discretize
   * @param threshold cutoff value for converting to nonzero, i.e., if |data[i]| < threshold, then output[i] = 0 
   * @return
   */
  public static byte[] discretizeArray(float[] data, float threshold) {
    byte[] output = new byte[data.length];
    
    for (int i = 0; i != data.length; i++) {
      if (data[i] >= threshold) {    // greater than threshold indicates it is a "rise
        output[i] = 1;
      }
      else if (data[i] <= -threshold) {  // less than threshold indicates it is a
        output[i] = -1;                 // a "fall"
      }
      else {                // within band to be a "zero" value
        output[i] = 0;
      }
    }
    
    return output;
  }
  
  /**
   * Detects locations of lines using light value readings. Applies difference filter
   * and analog-to-ternary conversion.
   * Detects a line by detecting a low-high after processing.
   * @param data unprocessed data array of light values
   * @return array containing locations of detected liens
   */
  private static float[] lineDetection(float[] data) {
    byte[] processedData = discretizeArray(differenceFilter(data), (float) 0.0325);
    
    float[] lineAngles = new float[40];
    int lineNumber = 0;
    
    int lastNegative = 0;
    int firstPositive = 0;
    boolean foundNegative = false; // indicates we must begin search for positives
    for (int i = 0; i != data.length; i++) {
      if (processedData[i] == -1) {
        lastNegative = i;
        foundNegative = true;
      }
      if (foundNegative) {
        if (processedData[i] == 1) {
          firstPositive = i;
          lineAngles[lineNumber] = (float) ((lastNegative*SAMPLING_PERIOD/1000.0 * BASE_SPEED +
              firstPositive*SAMPLING_PERIOD/1000.0 * BASE_SPEED)/2.0);
          lineNumber++;
          i += (int) angleSkip * SAMPLES / 360; // skip by ~5 degrees
        }
      }
    }
    
    return lineAngles;
  }
}
