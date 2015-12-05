package src.handleDb;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.postgis.Geometry;
import org.postgis.LineString;
import org.postgis.LinearRing;
import org.postgis.MultiLineString;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgis.Polygon;


/**
 * ジオメトリのパースをする
 * @author murase
 *
 */
public class GeometryParsePostgres {
	
	public static final int SRID = 4301;
	
	public GeometryParsePostgres(){
		
	}
	
	/**
	 * point型のデータをWKT形式のPointに変換
	 * @param aPoint
	 * @return
	 */
	public static String point2dString(Point2D.Double aPoint){
		String polygonString="POINT(";
		polygonString += aPoint.x + " " + aPoint.y;
		polygonString +=")";
		
		
		polygonString = "ST_PointFromText(\'"+polygonString+"\', "+SRID+")";
		
		return polygonString;
	}
	
	/**
	 * point型のデータをWKT形式のPointに変換
	 * @param aPoint
	 * @return
	 */
	public static String point2dStringEscape(Point2D.Double aPoint){
		String polygonString="POINT(";
		polygonString += aPoint.x + " " + aPoint.y;
		polygonString +=")";
		
		
		polygonString = "ST_PointFromText(\'\'"+polygonString+"\'\', "+SRID+")";
		
		return polygonString;
	}
	
	/**
	 * WKT形式のpointからPoint2D.DOubleに変換する
	 * @deprecated PGeometryを使ったものを使用すること
	 * @param aPointString
	 * @return
	 */
	public static Point2D.Double parsingPoint2d(String aPointString){
		Point2D.Double point2d = new Point2D.Double();
		String regex = "" +
				"\\(" +
				"(([1-9]\\d*|0)(\\.\\d+)?)" +
				"\\s" +
				"(([1-9]\\d*|0)(\\.\\d+)?)" +
				"\\)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(aPointString);
		
		while(matcher.find()){
			point2d = new Point2D.Double(
					java.lang.Double.parseDouble(matcher.group(1)), 
					java.lang.Double.parseDouble(matcher.group(4)));
		}
		return point2d;
	}
	
	/**
	 * PGeometry(MultiLineString)オブジェクトをLine2D.Doubleに変換
	 * @param aPointString
	 * @return
	 */
	public static ArrayList<Line2D> getMultiLineStringData(PGgeometry aGeom){
		ArrayList<Line2D> multiLineString = new ArrayList<>();
		if(aGeom.getGeoType() == Geometry.MULTILINESTRING){
			MultiLineString mLineString = (MultiLineString)aGeom.getGeometry(); 
			for( int r = 0; r < mLineString.numLines(); r++) { 
				LineString lineString = mLineString.getLine(r);
				multiLineString.add(
						(Line2D)new Line2D.Double(
								lineString.getPoint(0).x,
								lineString.getPoint(0).y,
								lineString.getPoint(lineString.numPoints()-1).x,
								lineString.getPoint(lineString.numPoints()-1).y));
			} 
		}
		return multiLineString;
	}
	
	/**
	 * MultiLineString((xx yy,xx yy),(xx yy,xx yy))からｘｘ、yyを取り出す
	 * WKT形式のPolygonからArrayList<Line2d>に変換する
	 * @deprecated PGeometryを使ったものを使用すること
	 * @param aStringLine
	 * @return
	 */
	public static ArrayList<Line2D> parsingMultiLine(String aStringLine){
		ArrayList<Line2D> line2ds = new ArrayList<>();
		String regex ="\\((\\d+\\.?\\d*)\\s*(\\d+\\.?\\d*)\\s*,\\s*(\\d+\\.?\\d*)\\s*(\\d+\\.?\\d*)\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(aStringLine);
		
		while(matcher.find()){
			line2ds.add(new Line2D.Double(
					java.lang.Double.parseDouble(matcher.group(1)),
					java.lang.Double.parseDouble(matcher.group(2)),
					java.lang.Double.parseDouble(matcher.group(3)),
					java.lang.Double.parseDouble(matcher.group(4))));
		}
		return line2ds;
	}
	
	/**
	 * ArrayList<Line2D>からst_MlineFromTextに変換する
	 * @deprecated PGeometryを使ったものを使用すること
	 * @param aMultiLine
	 * @return
	 */
	static public String multiLineString(ArrayList<Line2D> aMultiLine){
		// MultiLineString((),())の作成.
		String multiLineStiring = "'MultiLineString(";
		for(int i=0; i<aMultiLine.size(); i++){
			multiLineStiring +="("+aMultiLine.get(i).getX1()+" "+aMultiLine.get(i).getY1()+"";
			multiLineStiring +=",";
			multiLineStiring +=""+aMultiLine.get(i).getX2()+" "+aMultiLine.get(i).getY2()+"),";
		}
		multiLineStiring = multiLineStiring.substring(0, multiLineStiring.length()-1);
		multiLineStiring +=")'";
		
		String lineFromText = "st_MlineFromText("+multiLineStiring+", "+SRID+")";
		return lineFromText;
	}
	
	/**
	 * WKT形式のPolygonから緯度経度を取り出す
	 * @deprecated PGeometryを使ったものを使用すること
	 * @see GeometryParsePostgres#pgGeometryPolygon()
	 * @param aPolygonString
	 * @return
	 */
	public static ArrayList<Point2D.Double> parsingPolygon(String aPolygonString){
		String regex ="\\d[^,)]+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(aPolygonString);
		
		ArrayList<Point2D.Double> polygonArrayList = new ArrayList<>();
		
		while(matcher.find()){
			String[] lnglatStrings = matcher.group().split(" ");
			polygonArrayList.add(new Point2D.Double(
					java.lang.Double.parseDouble(lnglatStrings[0]),
					java.lang.Double.parseDouble(lnglatStrings[1])));
		}
		return polygonArrayList;
	}
	
	/**
	 * ArrayListからWKT形式のPolygonを返す
	 * @param anode
	 * @return
	 */
	public static String polygonString(ArrayList<Point2D.Double> aMultiNode){
		String polygonWKT = "'Polygon((";
		
		for(int i=0; i<aMultiNode.size(); i++){
			polygonWKT += ""+aMultiNode.get(i).x + " " + aMultiNode.get(i).y + ",";
		}
		polygonWKT = polygonWKT.substring(0, polygonWKT.length()-1);
		polygonWKT +="))'";
		polygonWKT = "st_polygonFromText("+polygonWKT+", "+SRID+")";
		return polygonWKT;
	}
	
	/**
	 * PGgeometryからPolygonに変換
	 */
	public static ArrayList<Point2D.Double> pgGeometryPolygon(PGgeometry geom){
		ArrayList<Point2D.Double> polygonGeom = new ArrayList<>();
		
		if(geom.getGeoType() == Geometry.POLYGON){
			Polygon pl = (Polygon)geom.getGeometry(); 
			for( int r = 0; r < pl.numRings(); r++) { 
				LinearRing rng = pl.getRing(r); 
//				System.out.println("Ring: " + r); 
				for( int p = 0; p < rng.numPoints(); p++ ) { 
					Point pt = rng.getPoint(p); 
//					System.out.println("Point: " + p);
//					System.out.println(pt.toString()); 
//					System.out.println(pt.x +"  "+pt.y);
					polygonGeom.add(new Point2D.Double(pt.x, pt.y));
				}
			}
		}
		return polygonGeom;
	}
	
	
	/**
	 * PGgeometryからpoint2Dに変換
	 * @param geom
	 * @return
	 */
	public static Point2D.Double pgGeometryToPoint2D(PGgeometry geom){
		Point2D.Double point = new Point2D.Double();
		
		if(geom.getGeoType() == Geometry.POINT){
			Point geometryPoint = (Point)geom.getGeometry();
			point = new Point2D.Double(geometryPoint.getX(), geometryPoint.getY());
		}
		return point;
	}
	
}
