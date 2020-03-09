package ca.mcgill.ecse211.project;


import lejos.hardware.ev3.LocalEV3;

import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * This class is used to define static resources in one place for easy access and to avoid
 * cluttering the rest of the codebase. All resources can be imported at once like this:
 * 
 * <p>{@code import static ca.mcgill.ecse211.lab5.Resources.*;}
 */
public class Resources {
  
  /**
   * Value of Pi
   */
  public static final double PI = 3.141592653589793238462643383279;
  
  /**
   * The wheel radius in centimeters.
   */
  public static final double WHEEL_RAD = 2.17;
  //was 2.13
  
  /**
   * The robot width in centimeters.
   */
  public static final double BASE_WIDTH = 15.1;
  
  /**
   * The distance from the ultrasonic sensor from the center of rotation
   */
  public static final double US_DIST = 6.5;
  
  /**
   * Distance of ultrasonic sensor from wheel base (cm)
   */
  public static final double SENSOR_DIST = 8;
  
  /**
   * Distance of color sensor from wheel base (cm)
   */
  public static final double CL_DIST = 15.5; 
  
  /**
   * The speed at which the robot moves forward in degrees per second.
   */
  public static final int FORWARD_SPEED = 200;
  
  /**
   * The speed at which the robot rotates in degrees per second.
   */
  public static final int ROTATE_SPEED = 150;
  
  /**
   * The motor acceleration in degrees per second squared.
   */
  public static final int ACCELERATION = 1000;
  
  /**
   * Timeout period in milliseconds.
   */
  public static final int TIMEOUT_PERIOD = 3000;
  
  /**
   * The tile size in centimeters. Note that 30.48 cm = 1 ft.
   */
  public static final double TILE_SIZE = 30.48;
  
  /**
   * Correction for one wheel being stronger than the other
   */
  public static final int WHEEL_BIAS = 3;
  
  /**
   * The limit of invalid samples that we read from the US sensor before assuming no obstacle.
   */
  public static final int INVALID_SAMPLE_LIMIT = 20;
  
  /**
   * Angle increments to take samples at while waiting until approaching a local min (degrees)
   */
  public static final double PHASE_ONE_ANGLE = 20;
  
  /**
   * Angle increments to take samples at while passing local min (degrees)
   */
  public static final int PHASE_TWO_ANGLE = 5;
  
  /**
   * The angle at which the robot realizes that it has fully passed the min.
   * Determined experimentally
   */
  public static final double OVERSHOOT = 18;
  
  /**
   *The left motor.
   */
  public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
  
  /**
   * The right motor.
   */
  public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.D);
  
  /**
   * The ultrasonic sensor.
   */
  public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
  
  /**
   * The color sensor used for color detection
   */
  public static final EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S3);
  
  /**
   * The color sensor used for light localization
   */
  public static final EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S2);
  
  /**
   * Instance of the odometer
   */
  public static Odometer odometer = new Odometer();
  
  /**
   * Instance of the navigation
   */
  public static Navigation navigator = new Navigation(odometer);
  
  /**
   * Instance of the light localizer
   */
  public static LightLocalizer lightLocalizer = new LightLocalizer();
  
  // Hardware initialization
  
  /**
   * The LCD screen used for displaying text.
   */
  public static final TextLCD lcd = LocalEV3.get().getTextLCD();
  
}
