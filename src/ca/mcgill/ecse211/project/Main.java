package ca.mcgill.ecse211.project;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Resources.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;

/**
 * The main driver class for the design project.
 */
public class Main {
  
  /**
   * The main entry point.
   * 
   * @param args not used
   */    
  public static void main(String[] args) throws InterruptedException {
    while (Button.waitForAnyPress() == Button.ID_ESCAPE)
      System.exit(0);
  }
  
  /**
   * Sleeps current thread for the specified duration.
   * 
   * @param duration sleep duration in milliseconds
   */
  public static void sleepFor(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      // There is nothing to be done here
    }
  }
}