package robot.coordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import robot.connector.ConnectorModule;
import robot.coordinates.parser.SimpleCoordinatesParser;

public class PositionCommander extends Thread{

	private ObjectMapper jsonMapper = new ObjectMapper();
	private final String ROS_COMMAND = "rostopic echo /global_pose";
	//private final String ROS_COMMAND = "cat samples/temp.txt";
	private final int POSITION_CHECK_INTERVAL = 1500;
	private final String LOCAL_NOTIFICATIONS_TOPIC = "demos/fphag/coordinates";
	private final String DATATYPE = "location";
	private final int TOTAL_READ_PER_SAMPLE = 75;
	private boolean simulationMode = false;
	private SimpleCoordinatesParser parser;
	private ConnectorModule connector;
	private UnityRepresentator unity;
	
	public PositionCommander(ConnectorModule connector, boolean simulationMode){
		this.parser = new SimpleCoordinatesParser();
		this.connector = connector;
		//this.unity = new UnityRepresentator();
		this.simulationMode = simulationMode;
	}
	
	public void run() {
		Runtime rt = Runtime.getRuntime();
		String line = "";
        int counter = 0;
        Process p = null;
        BufferedReader reader = null;
        String totalContent = "";
        Axis3D currentPosition = null;
        while(true){
			try {
				Thread.sleep(this.POSITION_CHECK_INTERVAL);
				
				if( this.simulationMode ){
					this.sendPosition(new Axis3D(1, 1, 1));
					continue;
				}
				p = rt.exec(this.ROS_COMMAND);
				reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	            line = null;
	            counter = 0;
	            totalContent = "";
	            while ((line = reader.readLine()) != null) {
	            	totalContent += line + " ";
	                counter++;
	                if( counter > this.TOTAL_READ_PER_SAMPLE ){
	                	p.destroy();
	                	break;
	                }
	            }
	            reader.close();
	            currentPosition = this.parser.getCoordinates(totalContent);
	            if( currentPosition != null ){
	            	System.out.println("[PositionCommander] - Robot Position[Robot Map]: " + currentPosition);
	            	this.sendPosition(currentPosition);
				}else{
					System.out.println("Error - [PositionCommander] - Unable to extract position from  ECHO Command...");
				}
			} catch (InterruptedException e) {
				System.out.println("Error - [PositionCommander] - Interrupted Exception");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error - [PositionCommander] - IOException");
			}
		}

    }
	
	public void sendPosition(Axis3D coordinates){
		ServiceData data = new ServiceData();
		try {
			data.setDatatype(this.DATATYPE);
			data.setDevice("ROBOT_AALHOUSE");
			data.setPayload(coordinates.getX()+","+coordinates.getY()+","+coordinates.getZ());
			this.connector.publish(this.LOCAL_NOTIFICATIONS_TOPIC, this.jsonMapper.writeValueAsBytes(data));
		} catch (JsonProcessingException e) {
			System.out.println("Error - [PositionCommander] -Unable to send Robot Position..");
		}
	}
	
}
