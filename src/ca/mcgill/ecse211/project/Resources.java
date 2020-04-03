package ca.mcgill.ecse211.project;


import java.math.BigDecimal;
import java.util.Map;
import ca.mcgill.ecse211.playingfield.Point;
import ca.mcgill.ecse211.playingfield.Region;
import ca.mcgill.ecse211.wificlient.WifiConnection;
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
   * The maximum distance the robot can go before it needs to re-localize (tile units)
   */
  public static final double MAX_DISTANCE = 3.5;
  
  /**
   *The left motor.
   */
  public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
  
  /**
   * The right motor.
   */
  public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.D);
  
  /** 
   * The right arm motor
   */
  public static final EV3LargeRegulatedMotor leftArmMotor = new EV3LargeRegulatedMotor(MotorPort.B);
  
  /** 
   * The right arm motor
   */
  public static final EV3LargeRegulatedMotor rightArmMotor = new EV3LargeRegulatedMotor(MotorPort.C);
  
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
  
  // WIFI PARAMETERS
  
  /**
   * The default server IP used by the profs and TA's.
   */
  public static final String DEFAULT_SERVER_IP = "192.168.2.3";
  
  /**
   * The IP address of the server that transmits data to the robot. For the beta demo and
   * competition, replace this line with
   * 
   * <p>{@code public static final String SERVER_IP = DEFAULT_SERVER_IP;}
   */
  public static final String SERVER_IP = "192.168.2.3"; // = DEFAULT_SERVER_IP;
  
  /**
   * Your team number.
   */
  public static final int TEAM_NUMBER = 5;
  
  /** 
   * Enables printing of debug info from the WiFi class. 
   */
  public static final boolean ENABLE_DEBUG_WIFI_PRINT = true;
  
  /**
   * Enable this to attempt to receive Wi-Fi parameters at the start of the program.
   */
  public static final boolean RECEIVE_WIFI_PARAMS = true;
  
  /**
   * Container for the Wi-Fi parameters.
   */
  public static Map<String, Object> wifiParameters;
  
  // This static initializer MUST be declared before any Wi-Fi parameters.
  static {
    receiveWifiParameters();
  }
  
  /** Red team number. */
  public static int redTeam = getWP("RedTeam");

  /** Red team's starting corner. */
  public static int redCorner = getWP("RedCorner");

  /** Green team number. */
  public static int greenTeam = getWP("GreenTeam");

  /** Green team's starting corner. */
  public static int greenCorner = getWP("GreenCorner");

  /** The Red Zone. */
  public static Region red = makeRegion("Red");

  /** The Green Zone. */
  public static Region green = makeRegion("Green");

  /** The Island. */
  public static Region island = makeRegion("Island");

  /** The red tunnel footprint. */
  public static Region tnr = makeRegion("TNR");

  /** The green tunnel footprint. */
  public static Region tng = makeRegion("TNG");

  /** The red search zone. */
  public static Region szr = makeRegion("SZR");

  /** The green search zone. */
  public static Region szg = makeRegion("SZG");
  
  /**
   * Receives Wi-Fi parameters from the server program.
   */
  public static void receiveWifiParameters() {
    // Only initialize the parameters if needed
    if (!RECEIVE_WIFI_PARAMS || wifiParameters != null) {
      return;
    }
    System.out.println("Waiting to receive Wi-Fi parameters.");

    // Connect to server and get the data, catching any errors that might occur
    try (WifiConnection conn =
        new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT)) {
      /*
       * getData() will connect to the server and wait until the user/TA presses the "Start" button
       * in the GUI on their laptop with the data filled in. Once it's waiting, you can kill it by
       * pressing the back/escape button on the EV3. getData() will throw exceptions if something
       * goes wrong.
       */
      wifiParameters = conn.getData();
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
  
  /**
   * Returns the Wi-Fi parameter int value associated with the given key.
   * 
   * @param key the Wi-Fi parameter key
   * @return the Wi-Fi parameter int value associated with the given key
   */
  public static int getWP(String key) {
    if (wifiParameters != null) {
      return ((BigDecimal) wifiParameters.get(key)).intValue();
    } else {
      return 0;
    }
  }
  
  /** 
   * Makes a point given a Wi-Fi parameter prefix.
   */
  public static Point makePoint(String paramPrefix) {
    return new Point(getWP(paramPrefix + "_x"), getWP(paramPrefix + "_y"));
  }
  
  /**
   * Makes a region given a Wi-Fi parameter prefix.
   */
  public static Region makeRegion(String paramPrefix) {
    return new Region(makePoint(paramPrefix + "_LL"), makePoint(paramPrefix + "_UR"));
  }
  
}
