package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import static ca.mcgill.ecse211.project.DriveUtil.turnBy;
import static ca.mcgill.ecse211.project.DriveUtil.moveStraightFor;


public class UltrasonicLocalizer {
  
  /**
   * The distance remembered by the {@code filter()} method.
   */
  private int filterDistance;

  /**
   * The number of invalid samples seen by {@code filter()} so far.
   */
  private int invalidSampleCount;
  
  /**
   * Buffer (array) to store US samples. Declared as an instance variable to avoid creating a new
   * array each time {@code readUsSample()} is called.
   */
  private float[] usData = new float[usSensor.sampleSize()];
  
  /**
   * Current distance read by the ultrasonic sensor
   */
  private static double distance;
  
  /**
   * Previous distance read by the ultrasonic sensor
   */
  private static double prevDistance = 0;
  
  /**
   * Distance to the wall we converge to
   */
  private static double d1;
  
  /**
   * Distance to the wall we don't converge to
   */
  private static double d2;
  
  /**
   * Flag shows which wall we were looking at
   */
  private static boolean sawInfinity = false;
  
  /**
   * Flag shows if localization is complete
   */
  private static boolean isComplete = false;
  
  /**
   * Perform angle localization, wait for a button press, then localize position
   */
  public void doLocalization() {
    distance = readUsDistance();
    // perform loop until we've begun approaching a min
    approachMin();
    
    passMin(PHASE_TWO_ANGLE,10); // overshoot the local min
    
    turnBy(-OVERSHOOT, false); // turn back by angle we overshot the local min 
    d1 = readAverageDistance(5) + US_DIST; //save first wall we converged to
    
    // look to see if it's infinity
    turnBy(90.0, false);
    //distance = readAverageDistance(5);
    usSensor.fetchSample(usData, 0);
    distance = (int) (usData[0] * 100);
    if (distance > TILE_SIZE) {
      turnBy(180.0, false); // look at other wall
      sawInfinity = true;
    }
    d2 = readAverageDistance(5) + US_DIST;
    
    if (sawInfinity) {  // seeing infinity indicates we need to turn 90 degrees
      turnBy(180.0, false);      // to see the zero 
    }
    else {              // not looking at infinity indicates our 0 degree
      turnBy(90.0, false);     // point is directly behind us
    }
    
    turnBy(45.0, false);
    
    moveStraightFor(1-((d1+d2)/2)/TILE_SIZE); // move to the (1,1) position
    turnBy(-45.0, false); // turn back to the 0 degree view
    
    odometer.setXyt(TILE_SIZE, TILE_SIZE, 0);
  }

  /**
   * Make large turns until a local minimum is being approached
   */
  private void approachMin() {
    while (distance >= prevDistance) {  // current distance being greater than previous reading
      turnBy(PHASE_ONE_ANGLE, false);          // indicates we are not yet approaching a min
      prevDistance = distance;
      distance = readAverageDistance(20); // one sample should be accurate enough
    }
  }
  
  /**
   * Turns until it detects the local min is passed
   * @param angleIncrement angle increment at which samples are taken in degrees
   * @param sampleCount amount of samples to take at each angle increment
   */
  private void passMin(double angleIncrement, int sampleCount) {
    while (distance <= prevDistance) {  // current distance being less that previous reading
      turnBy(angleIncrement, false);           // indicates we have not yet passed the min
      prevDistance = distance;
      distance = readAverageDistance(sampleCount);
    }
  }
  
  /**
   * Returns the filtered distance between the US sensor and an obstacle in cm.
   * 
   * @return the filtered distance between the US sensor and an obstacle in cm
   */
  private int readUsDistance() {
    usSensor.fetchSample(usData, 0);
    // extract from buffer, convert to cm, cast to int, and filter
    return filter((int) (usData[0] * 100.0));
  }

  /**
   * Rudimentary filter - toss out invalid samples corresponding to null signal.
   * 
   * @param distance raw distance measured by the sensor in cm
   * @return the filtered distance in cm
   */
  private int filter(int distance) {
    if (distance >= 255 && invalidSampleCount < INVALID_SAMPLE_LIMIT) {
      // bad value, increment the filter value and return the distance remembered from before
      invalidSampleCount++;
      return filterDistance;
    } else {
      if (distance < 255) {
        // distance went below 255: reset filter and remember the input distance.
        invalidSampleCount = 0;
      }
      filterDistance = distance;
      if (distance > TILE_SIZE)
        distance = (int) (TILE_SIZE); //avoid detecting the walls we 
      return distance;
    }
  }
  
  /**
   * Averages distances read by the ultrasonic sensor
   * @param samples amount of samples to average
   * @return average distance read
   */
  private double readAverageDistance(int samples) {
    double sum = 0;
    for (int i = 0; i != samples; i++) {
      sum += readUsDistance();
    }
    return sum / samples;
  }
}
