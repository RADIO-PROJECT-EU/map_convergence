package robot.localization.main;

import robot.connector.GatewayConnector;
import robot.coordinates.PositionCommander;

public class Main {

	public static void main(String[] args) {
		
		if( args.length != 1 ){
			System.out.println("[Usage] - java -jar <network>");
			System.exit(0);
		}
		
		boolean simulationMode = false;
		if( args.length > 1 ){
			simulationMode = Boolean.parseBoolean(args[1]);
		}
		
		System.out.println("[Main] - Starting up Robot Observator - Initializing Connection with Gateway...");
		GatewayConnector connector = new GatewayConnector(args[0]);
		connector.initialize();
		connector.connect();
		
		if( connector.isConnected() ){
			System.out.println("[Main] - Connection with Gateway established...");
			System.out.println("[RobotLocalization] - Starting up Robot localization...");
			new PositionCommander(connector,simulationMode).start();	
		}else{
			System.out.println("[Main] - Unable to establish connection with Gateway, Exiting...");
			System.exit(0);
		}
		
		
	}

}
