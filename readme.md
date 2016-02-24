# 概要
道路データ
(
	データベース名
	osm_road_db
	テーブル名
	osm_japan_car_2po_4pgr
)
から
トポロジを作成

## 設定
/src/src/handleDb/createDb/CreateTopoTable.javaにDBの作成先の情報を書いてください

``` java:
	private static final String DBNAME = "osm_road_topo_db";	// Database Name
	private static final String TBNAME = "";	
	private static final String TOPO_NAME = "topo_tb";	
	private static final String USER = "postgres";			// user name for DB.
	private static final String PASS = "usadasql";		// password for DB.
	private static final String URL = "各自の環境に合わせてDBの設定してください";//"rain2.elcom.nitech.ac.jp";
	private static final int PORT = 5432;
	private static final String DBURL = "jdbc:postgresql://"+URL+":"+PORT+"/" + DBNAME;
```
