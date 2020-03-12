package ca.mcgill.ecse211.project;

/**
 * 
 * The payload collection class localizes payloads (among other obstacles)
 * and checks if the detected object is the payload
 * 
 * @author Ricky
 *
 */
public class PayloadCollection {
  /**
   * Localizes potential payloads by sweeping with the ultrasonic sensor
   */
  void localizePayload() {
    
  }
  
  /**
   * Collects the payload
   */
  void collectPayload() {
    
  }
  
  /**
   * Applies a difference filter to an input array
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
