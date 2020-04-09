package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import java.util.ArrayList;
import lejos.hardware.Sound;

/**
 * Searches for colors during navigation.
 * 
 * @author Ricky
 *
 */
public class ColorDetection implements Runnable {

  /**
   * Array to store color ids
   */
  private static int[] colorIds = new int [15];
  
  /**
   * Arraylist that stores all colors detected during navigation
   */
  private static ArrayList<String> colorsDetected = new ArrayList<>();

  /**
   * Polls the color sensor and interrupts navigation when a color is
   * detected. Instructions for implementations of this function can
   * be found in section 8.3 of the software document.
   */
  public void run() {
    while (true) {
      lcd.clear();
      int[] colorIds = new int [15];
      for (int i = 0; i < colorIds.length;i++) {
        colorIds[colorSensor.getColorID()+1] += 1;
        int currentMax = colorIds[0];
        int currentMaxIndex = 0;
        for(int j = 1; j < colorIds.length; j++) {
          if(colorIds[j] > currentMax) {
            currentMaxIndex = j;
            currentMax = colorIds[j];
          }
        }
        String color = getColorString(currentMaxIndex);
        //if (color.equals("Black"))
          //DriveUtil.setSpeed(80);
        if (color.equals("Orange") || color.equals("Green") || color.equals("Yellow") || color.equals("Blue")) {
          //DriveUtil.stopMotors();
          DriveUtil.setSpeed(1);
          lcd.drawString("Object Detected", 1, 1);
          lcd.drawString(color, 1, 2);
          colorsDetected.add(color);
          Sound.twoBeeps();
          try {
            Thread.sleep(10000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          DriveUtil.setSpeed(FORWARD_SPEED);
          //break;
        }
      }
    }
  }
  
  /**
   * returns string from getColorID()
   * @param index the number returned by getColorID()
   * @return String color detected
   */
  private static String getColorString(int index) {
    switch (index) {
      case 0: return "None";
      case 1: case 6: return "Orange";
      case 2: case 7: return "Green";
      case 3: return "Blue";
      case 4: case 14: return "Yellow";
      case 8: return "Black";
      default: return "not a color";
    }
  }
  
  /**
   * Getter for ArrayList colorsDetected
   * @return colorsDetected
   */
  public static ArrayList<String> getColorsDetected() {
    return colorsDetected;
  }

}
