package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.DriveUtil.*;
import static ca.mcgill.ecse211.project.Resources.*;
import static ca.mcgill.ecse211.project.Main.sleepFor;

/**
 * 
 * Defines tools for navigating the map
 * 
 * @author Ricky
 *
 */
public class Navigation {
  
  private Odometer odo;
  
  private double[] currentXyt;
  
  Navigation(Odometer odo) {
    this.odo = odo;
  }
  
  /**
   * This method causes the robot to travel to the absolute field 
   * location (x, y), specified in tile points
   * 
   * @param x x coordinate of tile point
   * @param y y coordinate of tile point
   */
  public void travelTo(double x, double y) {
    currentXyt = odo.getXyt();
    double dy = y - currentXyt[1]/TILE_SIZE;
    double dx = x - currentXyt[0]/TILE_SIZE;
    double theta = Math.atan2(dx, dy) * 180/Math.PI;
        
    if (theta < 0)
      theta += 360;
        
    turnTo(theta);
    double deltaDistance = Math.sqrt(dx*dx + dy*dy);
    moveStraightFor(deltaDistance);
  }
  
  /**
   * This method causes the robot to turn (on point) to the absolute 
   * heading theta. This method turns a MINIMAL angle to its target.
   *
   * @param theta heading angle to turn to
   */
  public void turnTo(double theta) {
    double currentTheta = odometer.getTheta();
    
    double dTheta = theta - currentTheta;
    
    if (dTheta > 180)
      dTheta -= 360;
    else if (dTheta < -180)
      dTheta += 360;
    
    turnBy(dTheta, false);
  }
}

