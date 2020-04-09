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
  
  /**
   * Buffer (array) to store US samples. Declared as an instance variable to avoid creating a new
   * array each time {@code readUsSample()} is called.
   */
  private float[] usData = new float[usSensor.sampleSize()];
  
  /**
   * amount to change the angle heading by to avoid an object
   */
  private double headingChange = 30;
  
  Navigation(Odometer odo) {
    this.odo = odo;
  }
  
  /**
   * This method causes the robot to travel to the absolute field 
   * location (x, y), specified in tile points. Recursively breaks 
   * the paths into smaller pieces and checks if there is an obstacle
   * in the way. Redirects if necessary.
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
    double[] displacements;
    double theta = Math.atan2(dx, dy) * 180/Math.PI;
    if (theta < 0)
      theta += 360;
    double distance = magnitude(dx,dy); //calculate how far we have to go
    
    // perform iterative process to get closer to the waypoint without losing track
    
    if (distance < TILE_SIZE/2) {   // i.e., if we're at the waypoint within some margin of error
                                    //-- need some point to break
      
      if (distance > MAX_DISTANCE) { // check if we're going too far to recover our location
        displacements = breakUpPath(x, y, theta, currentX, currentY);
        travelTo(currentX + displacements[0], currentY + displacements[0]);
        
        // need to localize after moving to an intermediate path-- localization at the end may sometimes
        // undesirable and thus is left to the user to do manually.
        lightLocalizer.doLocalization();
      }
      else { // path is short enough
        usSensor.fetchSample(usData, 0);
        if (usData[0] > distance) {// there is nothing in the way of the path
        // move to point
        turnTo(theta);
        moveStraightFor(distance);
        }
        else { // there is something in the path, need to redirect
          displacements = avoidObject(x, y, theta, currentX, currentY);
          travelTo(currentX + displacements[0], currentY + displacements[1]);
        }
      }
    }
  }
  
  /**
   * Creates an intermediate path when navigating
   * 
   * @param x end waypoint x destination
   * @param y end waypoint y destination
   * @param theta required heading angle for direct path
   * @param currentX current x position
   * @param currentY current y position
   * @return [dx, dy] array for recommended intermediate displacement
   */
  private double[] breakUpPath(double x, double y, double theta, double currentX, double currentY) {
    double[] displacements = new double[2];
    // create vector components in the direction of the point
    double dx = MAX_DISTANCE * Math.sin(theta * Math.PI / 180);
    double dy = MAX_DISTANCE * Math.cos(theta * Math.PI / 180);
    
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
    
    displacements[0] = dx;
    displacements[1] = dy;
    
    return displacements;
  }
  
  /**
   * Provides a pair of [dx, dy] that will go around an object. Add or subtracts headingChange
   * from the current path if there is an object in the way of the desired path. Instructions
   * for implementation of this function can be found in section 8.1 of the software doc.
   * 
   * @param x x coordinate of final destination
   * @param y y coordinate of final destiation
   * @param theta heading angle
   * @param currentX current x position
   * @param currentY current y position
   * @return [dx,dy] array for recommended intermediate displacement
   */
  private double[] avoidObject(double x, double y, double theta, double currentX, double currentY) {
    double[] displacements = new double[2];
    
    // function body. Will create a new vector [dx,dy] vector with heading angle theta +- heading angle
    // to avoid the object. Will look at the floor/ceil options for [dx,dy] to find a path that does
    // not exceed the max distance and puts the robot closest to where it is heading.
    
    return displacements;
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

