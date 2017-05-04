package robot.coordinates.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.coordinates.Axis3D;

public class SimpleCoordinatesParser {
	
	private final String COORDINATES_SPLITTER = " ";
	private final String COORDINATES_REGEX_PATTERN = "(x:\\s+[-0-9\\.]+\\sy:\\s+[-0-9\\.]+\\stheta:\\s+[-0-9\\.]+\\s)";
	private Matcher matcher;
	private Pattern pattern;
	
	/**
	 * Main Constructor for the parser
	 * @param filepath - The fullpath from the 
	 */
	public SimpleCoordinatesParser(){
		this.pattern = Pattern.compile(this.COORDINATES_REGEX_PATTERN);
	}
	
	public Axis3D getCoordinates(String coordinates){
		this.matcher = pattern.matcher(coordinates);
		Axis3D coords = null;
		while( matcher.find() ){
			coords = this.extractCoordinates(matcher.group(1));
		}
		return coords;
	}

	private Axis3D extractCoordinates(String content) {
		String[] temp = content.split(this.COORDINATES_SPLITTER);
		int length = temp.length;
		if( length == 0 ) {
			System.out.println("[SimpleCoordinatesParser] - Unable to parse Coordinates [Splitter = "+this.COORDINATES_SPLITTER+"] ");
			return null;
		}
		double x,y,z= 0;
		try{
			x = Double.parseDouble(temp[1]);
			y = Double.parseDouble(temp[3]);
			z = Double.parseDouble(temp[5]);
		}catch(NumberFormatException e){
			System.out.println("[SimpleCoordinatesParser] - Unable to construct Double from String...");
			return null;
		}
		return new Axis3D(x, y, z);
	}

}
