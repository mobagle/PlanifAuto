package camera;

import java.util.ArrayList;

import utils.IntPoint;

public class Camera {

	private final ArrayList<IntPoint>			masterList;

	public Camera() {
		masterList = new ArrayList<IntPoint>();
	}
	
	/********************************************************
	 * Mise a jour de la carte avec le donnees serveurs
	 *******************************************************/

	synchronized private void cleanHashMap() {
		masterList.clear();
	}
	
	synchronized public void receiveRawPoints(final ArrayList<IntPoint> PointsList) {
		cleanHashMap();
		System.out.println("Reception des points :");
		for(IntPoint tp : PointsList){
			System.out.println(" - "+tp);
			masterList.add(tp);
		}
	}
	
	public ArrayList<IntPoint> getPaletsPositions() {
		return (ArrayList<IntPoint>) this.masterList.clone();
	}
	
	
}
