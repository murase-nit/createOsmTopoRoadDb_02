package src.handleDb.getData;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class RoadDataSet {
	
	public int id;
	public int source;
	public int target;
	public int clazz;
	public Point2D xy1;
	public Point2D xy2;
	public String geomString;
	public ArrayList<Point2D> geom;
	
	public RoadDataSet() {
	}
	
}
