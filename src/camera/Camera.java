package camera;

import java.util.ArrayList;

import utils.IntPoint;

public class Camera {

	private boolean seekLeft;
	
	private final ArrayList<IntPoint>			masterList;

	public Camera() {
		masterList = new ArrayList<IntPoint>();
		seekLeft = true;
	}
	
	public void setSeekLeft(boolean b) {
		seekLeft = b;
		System.out.println("seekLeft : "+seekLeft);
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
        	IntPoint p = this.traductionPoint(tp);
			System.out.println(" - "+tp);
			masterList.add(tp);
		}
	}
	
	public ArrayList<IntPoint> getPaletsPositions() {
		return (ArrayList<IntPoint>) this.masterList.clone();
	}
	
	private IntPoint traductionPoint(IntPoint intPoint) {
		if (this.seekLeft) {
			return traductionPointLeft(intPoint);
		} else {
			return traductionPointRight(intPoint);
		}
	}

	private IntPoint traductionPointRight(IntPoint intPoint) {
		int x = intPoint.getX();
		int y = intPoint.getY();
		int newX = (int)(Math.abs(200-x)/50)*3;
		int newY = (int)(x/50)*3;
		return new IntPoint(newX, newY);
	}

	private IntPoint traductionPointLeft(IntPoint intPoint) {
		int x = intPoint.getX();
		int y = intPoint.getY();
		int newX = (int)(x/50)*3;
		int newY = (int)(x/50)*3;
		return new IntPoint(newX, newY);
	}
	
}
