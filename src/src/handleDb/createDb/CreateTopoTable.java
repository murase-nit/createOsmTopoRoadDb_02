package src.handleDb.createDb;

import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.util.HashMap;

import src.handleDb.HandleDbTemplateSuper;


/**
 * トポロジーを作成する
 * @author murase
 *
 */
public class CreateTopoTable extends HandleDbTemplateSuper{
	private static final String DBNAME = "osm_road_topo_db";	// Database Name
	private static final String TBNAME = "";	
	private static final String TOPO_NAME = "topo_tb";	
	private static final String USER = "postgres";			// user name for DB.
	private static final String PASS = "usadasql";		// password for DB.
	private static final String URL = "各自の環境に合わせてDBの設定してください";//"rain2.elcom.nitech.ac.jp";
	private static final int PORT = 5432;
	private static final String DBURL = "jdbc:postgresql://"+URL+":"+PORT+"/" + DBNAME;
	
	
	/** sourceIdやtargetIdからトポロジ内でのノードIDが取得できる */
	public HashMap<Integer, Integer> relationNodeId = new HashMap<>();
	/** linkIdからトポロジ内でのリンクIDが取得できる */
	public HashMap<Integer, Integer> relationLinkId = new HashMap<>();
	
	
	public CreateTopoTable(){
		super(DBNAME, USER, PASS, DBURL, HandleDbTemplateSuper.POSTGRESJDBCDRIVER_STRING);
	}
	
	/**
	 * トポロジの作成準備
	 */
	public void createTopo(){
		try{
			String statement="";
			statement += "SELECT topology.CreateTopology('"+TOPO_NAME+"', "+WGS84_EPSG_CODE+");";
//			System.out.println(statement);
			ResultSet rs = executeTopo(statement);
			while(rs.next()){
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * トロポジへノードデータを格納する
	 */
	public void insertTopoNodeData(int nodeId, Point2D aPoint2d){
		try{
			String statement="";
			statement += "select st_addisonode('"+TOPO_NAME+"', 0, st_geomFromText('point("+aPoint2d.getX()+" "+aPoint2d.getY()+")', "+WGS84_EPSG_CODE+"));";
//			System.out.println(statement);
			ResultSet rs = executeTopo(statement);
			while(rs.next()){
				relationNodeId.put(nodeId, rs.getInt(1));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * トポロジへエッジデータを格納する
	 */
	public void insertTopoEdgeData(int aLinkId, int aSourceId, int aTargetId, String geomString){
		try{
			String statement="";
			statement += "select st_addedgemodface('"+TOPO_NAME+"', "+relationNodeId.get(aSourceId)+", "+relationNodeId.get(aTargetId)+", st_geomFromText('"+geomString+"',"+WGS84_EPSG_CODE+"));";
//			System.out.println(statement);
			ResultSet rs = executeTopo(statement);
			while(rs.next()){
				relationLinkId.put(aLinkId, rs.getInt(1));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
