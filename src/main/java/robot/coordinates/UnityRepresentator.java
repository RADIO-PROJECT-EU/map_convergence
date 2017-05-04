package robot.coordinates;

public class UnityRepresentator {
	
	private final double SCALE = 0.4;
	private final String ROBOT_MOVE_TEMPLATE = "RobotMove,<%x%>,<%y%>,0";
	private int POINT_X0 = -188;
	private int POINT_Y0 = -88;
	
	public UnityRepresentator(){}

	public double getScale() {
		return this.SCALE;
	}
	
	public Axis3D getUnityPosition(Axis3D position){
		double ucordx = ((position.getX()*100) * this.SCALE) + this.POINT_X0;
		double ucordy = ((position.getY()*100) * this.SCALE) + this.POINT_Y0;
		return new Axis3D(ucordx, ucordy,0);
	}
	
	public String getMoveCommandForPublish(Axis3D position){
		Axis3D transfromPos = this.getUnityPosition(position);
		String command = this.ROBOT_MOVE_TEMPLATE.replaceAll("<%x%>", String.format( "%.2f", transfromPos.getX() ));
		command = command.replace("<%y%>", String.format( "%.2f", transfromPos.getY()));
		return command;
	}

}
