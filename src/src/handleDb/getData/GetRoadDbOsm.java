package src.handleDb.getData;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;

import src.handleDb.HandleDbTemplateSuper;


/**
 * OMS道路データを取得
 * @author murase
 *
 */
public class GetRoadDbOsm extends HandleDbTemplateSuper{
	private static final String DBNAME = "osm_road_db";	// Database Name
	private static final String TBNAME = "osm_japan_car_2po_4pgr";
	private static final String TB_TMP_NAME = "tb_temp_osm_road";
	private static final String USER = "postgres";			// user name for DB.
	private static final String PASS = "usadasql";		// password for DB.
	private static final String URL = "rain2.elcom.nitech.ac.jp";
	private static final int PORT = 5432;
	private static final String DBURL = "jdbc:postgresql://"+URL+":"+PORT+"/" + DBNAME;

	
	public GetRoadDbOsm(){
		super(DBNAME, USER, PASS, DBURL, HandleDbTemplateSuper.POSTGRESJDBCDRIVER_STRING);
		
		
	}
	
	/**
	 * 一時テーブルを作成
	 */
	public void createTmpTable(){
		try{
			String statement="";
			statement = "create temp table "+TB_TMP_NAME+"(id serial, clazz integer, source integer, target integer, x1 double precision, y1 double precision, x2 double precision, y2 double precision, geom geometry);";
			statement += " create index idx_"+TB_TMP_NAME+"_id on "+TB_TMP_NAME+" using btree(id); ";
			statement += " create index idx_"+TB_TMP_NAME+"_clazz on "+TB_TMP_NAME+" using btree(clazz); ";
			statement += " create index idx_"+TB_TMP_NAME+"_source on "+TB_TMP_NAME+" using btree(source); ";
			statement += " create index idx_"+TB_TMP_NAME+"_target on "+TB_TMP_NAME+" using btree(target); ";
			statement += " create index idx_"+TB_TMP_NAME+"_geom on "+TB_TMP_NAME+" using gist(geom); ";
			System.out.println(statement);
			insertInto(statement);
			statement = " " +
						" insert into " +
						" tb_temp_osm_road(clazz, source, target, x1, y1, x2, y2, geom) " +
							" select " +
								"clazz, source, target, x1, y1, x2, y2, geom_way" +
							" from " +
								" " + TBNAME + " " +
							" where " +
								" clazz > 12;";
			System.out.println(statement);
			insertInto(statement);
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	
	
	
	// sourceとtargetの重複を取ったノードID列を取得.
	public HashSet<Integer> getDistinctNodeId(){
		HashSet<Integer> hashset = new HashSet<>();
		try{
			String s = "select source, target from "+TB_TMP_NAME+"";
			ResultSet rs = execute(s);
			while(rs.next()){
				hashset.add(rs.getInt("source"));
				hashset.add(rs.getInt("target"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return hashset;
	}
	
	// 指定したノードデータを取得.
	public Point2D getNodeInfo(int aNodeId){
		Point2D point2d = new Point2D.Double();
		try{
			String s = "select source, x1, y1 from "+TB_TMP_NAME+" where source ="+aNodeId+"";
			ResultSet rs = execute(s);
			if(rs.next()){
				point2d = new Point2D.Double(rs.getDouble("x1"), rs.getDouble("y1"));
			} else {
				s = "select target, x2, y2 from "+TB_TMP_NAME+" where target ="+aNodeId+"";
				rs = execute(s);
				if(rs.next()){
					point2d = new Point2D.Double(rs.getDouble("x2"), rs.getDouble("y2"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return point2d;
	}
	
	/**
	 * データ数を返す
	 * @return
	 */
	public int getLinkNum(){
		int dataNum = 0;
		try{
			String s = "select count(*) from "+TB_TMP_NAME+" ";
			ResultSet rs = execute(s);
			if(rs.next()){
				dataNum = rs.getInt(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return dataNum;
	}
	
	// リンクIDからリンクデータを取得.
	public RoadDataSet getLinkFromId(int aId){
		RoadDataSet roadDataSet = new RoadDataSet();
		try{
			String s = "select id, source, target, st_asText(geom) geomString from "+TB_TMP_NAME+" where id = "+aId+"";
			ResultSet rs = execute(s);
			if(rs.next()){
				roadDataSet.id = rs.getInt(1);
				roadDataSet.source = rs.getInt(2);
				roadDataSet.target = rs.getInt(3);
				roadDataSet.geomString = rs.getString(4);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return roadDataSet;
	}
	
	
	
	
	///////////////////////////////////////////////////////
	/////ここから//////////////////////////////////////////////////
	///////////////////////////////////////////////////////
	
	/**
	 * 道路データを取得し変数へ代入(範囲を指定する)
	 * @param aUpperLeftLngLat
	 * @param aLowerRightLngLat
	 */
	public void insertRoadData(Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat){
		try{
			String statement="";
			statement += "" +
						" select " +
							" id, clazz, source, target, x1, y1, x2, y2, geom" +
						" from " +
							" " + TBNAME + " " +
						" where " +
							"st_intersects(" +
								"st_polygonFromText(" +
									"'Polygon(("+aUpperLeftLngLat.getX() +" "+aLowerRightLngLat.getY()  +","+
												 aLowerRightLngLat.getX()+" "+ aLowerRightLngLat.getY() +","+
												 aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()   +","+
												 aUpperLeftLngLat.getX() +" "+aUpperLeftLngLat.getY()   +","+
												 aUpperLeftLngLat.getX() +" "+aLowerRightLngLat.getY()  +")" +
									")'" +
									"," +
									""+WGS84_EPSG_CODE+"" +
								")" +
								"," +
								"geom" +
							")" +
							" and " +
							" clazz > 12;";
										
//			System.out.println("create tmp table "+ statement);
			insertInto(statement);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
