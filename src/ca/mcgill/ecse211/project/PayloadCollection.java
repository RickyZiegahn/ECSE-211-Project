package ca.mcgill.ecse211.project;

/**
 * 
 * The payload collection class localizes potential payloads
 * and checks if the detected object is the payload, and
 * collects the payload.
 * 
 * @author Ricky
 *
 */
public class PayloadCollection {
  
  /**
   * Lines up with the payload, drops the arm, moves under the payload, and lifts it
   * on top of the robot.
   */
  public void collectPayload() {
    
  }
  
  /**
   * Looks for markers to confirm if the potential payload is what the robot
   * is looking for.
   */
  public boolean confirmPayload() {
    boolean isPayload = false;
    
    // function body will have to confirm that the potential payload
    // by searching for markers that would indicate that it is in fact
    // the payload
    
    return isPayload;
  }
  
  /**
   * Uses other functions to sweep a region, and apply filters. Will then process the data to detect
   * potential payloads.
   * 
   * @return array in the form [angle1, distance1, angle2, distance2, ...] showing heading and distance of objects
   */
  public double[] getAnglesDistance() {
    int size = 2*4; // assuming less than 4 objects
    double[] anglesDistances = new double[size]; //contains 
    
    // will need to use sweepDistances, apply the appropriate amount of difference filters (likely two)
    // and discretize the array. Process should be very similar to getLineAngles from LightLocalizer class.
    
    return anglesDistances;
  }
  
  /**
   * Sweeps the area while measuring distances with the ultrasonic. Turns clockwise
   * from current heading angle.
   * 
   * @param sweepAngle
   * @return array containing distances read
   */
  private float[] sweepDistances(double sweepAngle) {
    int size = (int) (sweepAngle/2); // will read every two degrees
    float[] distances = new float[size];
    
    // function body must turnBy sweepAngle and while it turns, poll
    // the ultrasonic distance sensor for distances
    
    return distances;
  }
  
  /**
   * Applies a difference filter to a (non-circular) input array
   * 
   * @param data array of data to apply difference filter to
   * @return filtered data array
   */
  private static float[] differenceFilter(float[] data) {
    float[] output = new float[data.length];
    
    for (int i = 1; i != data.length; i++) {
      output[i] = data[i] - data[i-1];
    }
    
    return output;
  }
}
