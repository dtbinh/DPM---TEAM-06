package main;

import java.io.IOException;
import java.util.HashMap;

import testingWifi.WifiConnection;

public class Main {

	private static String SERVER_IP = "192.168.43.74";
	private static int TEAM_NUMBER = 6;
	private static int startingCorner;
	private static int role;
	private static int goalWidth;
	private static int defenderLine;
	private static int forwardLine;
	private static int lowerLeftX;
	private static int lowerLeftY;
	private static int upperRightX;
	private static int upperRightY;
	private static int ballColour;
	public static void main(String[] args) 
	{
		parseParameters();
		
		print(startingCorner);
		print(role);
		print(goalWidth);
		print(defenderLine);
		print(forwardLine);
		print(lowerLeftX);
		print(lowerLeftY);
		print(upperRightX);
		print(upperRightY);
		print(ballColour);

	}
	private static void print(int param) 
	{
		System.out.print(param + ", ");
	}
	private static void parseParameters() //start wificonnection class, establish connection and set variables
	{
		WifiConnection conn = null;
		
		try {
			conn = new WifiConnection(SERVER_IP, TEAM_NUMBER);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (conn != null){
			
			HashMap<String,Integer> t = conn.StartData;
			if (t == null)
				System.out.println("Failed to read transmission");
			else {
				System.out.println("Transmission read");
				
				// initialize variables
				
				if(t.get("OTN").equals(TEAM_NUMBER)) 
				{//attacker
					role = 0;
					startingCorner = t.get("OSC");
				}
				else
				{//defender
					role = 1;
					startingCorner = t.get("DSC");
				}
				
				goalWidth = t.get("w1")*30;
				defenderLine = (11-t.get("d1"))*30;
				forwardLine = t.get("d2")*30;
				lowerLeftX = t.get("ll-x")*30;
				lowerLeftY = t.get("ll-y")*30;
				upperRightX = t.get("ur-x")*30;
				upperRightY = t.get("ur-y")*30;
				ballColour = t.get("BC");
				
			}
			
		} else {
			System.out.println("Transmission failed");
		}
				
	}
}
