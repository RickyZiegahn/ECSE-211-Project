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
 
    double currentX = currentXyt[0]/TILE_SIZE;
    double currentY = currentXyt[1]/TILE_SIZE;
    double dy = y - currentY;
    double dx = x - currentX;
    double theta = Math.atan2(dx, dy) * 180/Math.PI;
    if (theta < 0)
      theta += 360;
    double distance = magnitude(dx,dy); //calculate how far we have to go
    
    // perform iterative process to get closer to the waypoint without losing track
    while (distance < TILE_SIZE/2) {
      if (distance > MAX_DISTANCE) { // check if we're going too far to recover our location
        // create vector components in the direction of the point
        dx = MAX_DISTANCE * Math.sin(theta * Math.PI / 180);
        dy = MAX_DISTANCE * Math.cos(theta * Math.PI / 180);
        
        // Compute all the floored and ceiling values of dx, dy (so we end on a point so we can localize)
        double dxFloored = Math.floor(dx);
        double dxCeil = Math.ceil(dx);
        double dyFloored = Math.floor(dy);
        double dyCeil = Math.ceil(dy);
        
        // Compute the distance we have to move to get to one of the corners
        double case1 = magnitude(dxFloored, dyFloored);
        double case2 = magnitude(dxCeil, dyFloored);
        double case3 = magnitude(dxFloored, dyCeil);
        double case4 = magnitude(dxCeil, dyCeil);
        
        // Search for the adjacent point that leaves the robot closest to the
        // destination while still being under the maximum distance
        
        double minRemainingX = x - currentX;    // minimum remaining distance after moving
        double minRemainingY = y - currentY;    // (using distance remaining before movement as placeholder)
        double minRemainingDistance = magnitude(minRemainingX, minRemainingY);  // magnitude of remaining distance after movement
        
        if (case1 <= MAX_DISTANCE) {
          if (magnitude(x - (currentX + dxFloored), y - (currentY + dyFloored)) < minRemainingDistance) {
            minRemainingX = dxFloored;
            minRemainingY = dyFloored;
            minRemainingDistance = magnitude(x - (currentX + minRemainingX), y - (currentY + minRemainingY));
          }
        }
        if (case2 <= MAX_DISTANCE) {
          if (magnitude(x - (currentX + dxCeil), y - (currentY + dyFloored)) < minRemainingDistance) {
            minRemainingX = dxCeil;
            minRemainingY = dyFloored;
            minRemainingDistance = magnitude(x - (currentX + minRemainingX), y - (currentY + minRemainingY));
          }
        }
        if (case3 <= MAX_DISTANCE) {
          if (magnitude(x - (currentX + dxFloored), y - (currentY + dyCeil)) < minRemainingDistance) {
            minRemainingX = dxFloored;
            minRemainingY = dyCeil;
            minRemainingDistance = magnitude(x - (currentX + minRemainingX), y - (currentY + minRemainingY));
          }
        }
        if (case4 <= MAX_DISTANCE) {
          if (magnitude(x - (currentX + dxCeil), y - (currentY + dyCeil)) < minRemainingDistance) {
            minRemainingX = dxCeil;
            minRemainingY = dyCeil;
            minRemainingDistance = magnitude(x - (currentX + minRemainingX), y - (currentY + minRemainingY));
          }
        }
        dx = minRemainingX;
        dy = minRemainingY;
        distance = magnitude(dx, dy);
        
        // compute the new theta to get to our intermediate point
        theta = Math.atan2(dx, dy) * 180/Math.PI;
      }
      
      turnTo(theta);
      moveStraightFor(distance);
    }
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
  
  /**
   * Computes the magnitude of an (x,y) vector
   * 
   * @param x x coordinate of vector
   * @param y y coordinate of vector
   * @return the magnitude of the vector
   */
  public double magnitude(double x, double y) {
    return Math.sqrt(x*x + y*y);
  }
}

