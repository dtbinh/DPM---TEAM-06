package Robot;

import Robot.Navigator;
import Robot.Odometer;
import Robot.OdometryDisplay;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Main 
{
	
	//creating instances of motors for use
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	//public static final EV3LargeRegulatedMotor headMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	
	public static final Port usPort = LocalEV3.get().getPort("S1");
	public static final Port leftColorPort = LocalEV3.get().getPort("S2");
	public static final Port rightColorPort = LocalEV3.get().getPort("S3");
	
	//specifications about robot, global and should be accessible.
	public static double WHEEL_RADIUS = 2.09; //measure
	public static double TRACK = 14.46;
	public static double LS_DIST = 12.5;
	public static int bandCenter = 20;
	public static int bandWidth = 2;
	public static int motorLow = 100;
	public static int motorHigh = 200;
	public static int startingCorner = 1;
	
	//classes main will rely on
	public static Navigator nav;
	public static Odometer odometer;
	public static OdometryDisplay odometryDisplay;
	public static BallGrab ballGrabber;
	
	public static SensorModes usSensor = new EV3UltrasonicSensor(usPort);
	public static SampleProvider usValue = usSensor.getMode("Distance");
	public static float[] usData = new float[usValue.sampleSize()];
	
	public static SensorModes leftColorSensor = new EV3ColorSensor(leftColorPort);
	public static SampleProvider leftColorValue = leftColorSensor.getMode("Red");
	public static float[] leftColorData = new float[leftColorValue.sampleSize()];		
	
	public static SensorModes rightColorSensor = new EV3ColorSensor(rightColorPort);
	public static SampleProvider rightColorValue = rightColorSensor.getMode("Red");
	public static float[] rightColorData = new float[rightColorValue.sampleSize()];
	
	/**
	 * llX is the lower left x coordinate of the ball platform tile
	 * llY is the lower left y coordinate of the ball platform tile
	 * urX is the upper right  x coordinate of the ball platform tile
	 * urY is the upper right y coordinate of the ball platform tile
	 */
	public static int llX, llY, urX, urY, SC;
	
	
	public static void main(String[] args) 
	{
		odometer = new Odometer();
		odometer.start();
		odometryDisplay = new OdometryDisplay();
		odometryDisplay.start();
		nav = new Navigator();
		nav.start();
		ballGrabber = new BallGrab();
		
		//parseParameters();
		readyPosition();
		parseParameters();
		startPlaying();
	}
	
	private static void parseParameters() //start wificonnection class, establish connection and set variables
	{
		// these variables will store the parameters received from the Wifi connection class
		int ll_x = 0, ll_y = 0, ur_x = 0, ur_y = 0, sc = 0;
		
		ll_x *= 10; ll_y *= 10; ur_x *= 10; ur_y *= 10;
		
		// update the variables of this project to match the parameters received
		// from teh Wifi connection class
		llX = ll_x;
		llY = ll_y;
		urX = ur_x;
		urY = ur_y;
		SC = sc;
		
	}
	private static void readyPosition() //start localization
	{
		USLocalizer usL = new USLocalizer();
		usL.doLocalization();
		Button.waitForAnyPress();
		LineSquarer lineSquarer = new LineSquarer();
		lineSquarer.squareWithLines();
		Button.waitForAnyPress();
		//once you get here, the robot will be at what it thinks is 0,0 and heading in 0. If the starting corner was 1, this would be fine.
		
		switch(startingCorner)
		{
		case 1: 
			odometer.setX(0);
			odometer.setY(0);
			odometer.setTheta(0);
			System.out.println("Case 1");
			break;
		case 2:
			odometer.setX(300);
			odometer.setY(0);
			odometer.setTheta(0.5*Math.PI);
			System.out.println("Case 2");
			break;
		case 3:
			odometer.setX(300);
			odometer.setY(300);
			odometer.setTheta(Math.PI);
			System.out.println("Case 3");
			break;
		case 4:
			odometer.setX(0);
			odometer.setY(300);
			odometer.setTheta(1.5*Math.PI);
			System.out.println("Case 4");
			break;
		}
	}
	private static void startPlaying() //take role, make either attacker/defender class, transfer control to that class
	{	
		// travel to the region near the ball platform
		// where we can position the sensors to correctly identify the balls
		int destX = urX - 30;
		int destY = urY - 30;
		nav.travelTo(destX, destY);
		
		// make it turn south
		nav.turnTo(-Math.PI);
		
		// grab the ball
		ballGrabber.run();
	}
}