package camera;

import java.util.ArrayList;

public class Camera {

	private final ArrayList<IntPoint>			masterList;

	Camera() {
		masterList = new ArrayList<IntPoint>();
	}
	
	/********************************************************
	 * Mise a jour de la carte avec le donnees serveurs
	 *******************************************************/

	synchronized private void cleanHashMap() {
		for(IntPoint tp : masterList){
			masterList.remove(tp);
		}
	}
	
	synchronized public void receiveRawPoints(final int timeout, final ArrayList<IntPoint> PointsList) {
		cleanHashMap();
		for(IntPoint tp : PointsList){
			masterList.add(tp);
		}
	}
	
	public ArrayList<IntPoint> getPaletsPositions() {
		return this.masterList;
	}
	
	
}
