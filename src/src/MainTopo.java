package src;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Iterator;


import java.util.concurrent.CountDownLatch;

import src.handleDb.createDb.CreateTopoTable;
import src.handleDb.getData.GetRoadDbOsm;
import src.handleDb.getData.RoadDataSet;

public class MainTopo {

	/**
	 * 日本全国道路データを取得し，トポロジーを生成する
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		
		GetRoadDbOsm getRoadDbOsm = new GetRoadDbOsm();
		CreateTopoTable createTopoTable = new CreateTopoTable();
		getRoadDbOsm.startConnection();
		createTopoTable.startConnection();
		
		getRoadDbOsm.createTmpTable();
		createTopoTable.createTopo();
		
		// ノードデータを取得し，トポロジーとして格納.
		HashSet<Integer> distinctNodeId = getRoadDbOsm.getDistinctNodeId();
		Iterator<Integer> it = distinctNodeId.iterator();	// 重複を取ったノードID.
		int nodeNum = distinctNodeId.size();
		for(int count =0 ; it.hasNext(); count++){
			int value = it.next();
			createTopoTable.insertTopoNodeData(value, getRoadDbOsm.getNodeInfo(value));
			if(count % 1000 == 0){
				System.out.println("node "+count+"/"+nodeNum);
			}
		}
		
		// リンクデータを取得し，トポロジーとして格納.
		int linkNum = getRoadDbOsm.getLinkNum();
		for(int i=0+1; i< linkNum+1; i++){
			RoadDataSet roadDataSet = getRoadDbOsm.getLinkFromId(i);
			createTopoTable.insertTopoEdgeData(roadDataSet.id, roadDataSet.source, roadDataSet.target, roadDataSet.geomString);
			if(i % 100 == 0){
				System.out.println("link"+i+"/"+linkNum);
			}
		}
		
		
		getRoadDbOsm.endConnection();
		createTopoTable.endConnection();
		
		
		
		
	}

}
