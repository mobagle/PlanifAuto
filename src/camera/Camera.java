package camera;

import java.util.ArrayList;

import utils.IntPoint;

public class Camera {

	private boolean seekLeft;
	
	private final ArrayList<IntPoint>			masterList;

	public Camera() {
		masterList = new ArrayList<IntPoint>();
		seekLeft = true;
		System.out.println("seekLeft : "+seekLeft);
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
			System.out.println(" - "+p);
			masterList.add(p);
		}
	}
	
	public ArrayList<IntPoint> getPaletsPositions() {
		return (ArrayList<IntPoint>) this.masterList.clone();
	}

	private IntPoint traductionPoint(IntPoint intPoint) {
		int newX = findX(intPoint.getX());
		int newY = findY(intPoint.getY());
		if (!seekLeft) {
			newX = 12-newX;
			newY = 12-newY;
		}
		return new IntPoint(newX, newY);
	}

	private int findX(int x) {
		if (x<= 4) return 0;
		else if (x<= 21 && x >= 5) return 1;
		else if (x<= 34 && x >= 22) return 2;
		else if (x<= 48 && x >= 35) return 3;
		else if (x<= 65 && x >= 49) return 4;
		else if (x<= 89 && x >= 66) return 5;
		else if (x<= 105 && x >= 90) return 6;
		else if (x<= 122 && x >= 106) return 7;
		else if (x<= 138 && x >= 123) return 8;
		else if (x<= 162 && x >= 139) return 9;
		else if (x<= 180 && x >= 163) return 10;
		else if (x<= 194 && x >= 181) return 11;
		else if (x >= 195) return 12;
		else return -1;
	}
	
	private int findY(int y) {
		if (y<= 50 && y >= 38) return 0;
		else if (y<= 70 && y >= 39) return 1;
		else if (y<= 96 && y >= 71) return 2;
		else if (y<= 109 && y >= 97) return 3;
		else if (y<= 135 && y >= 110) return 4;
		else if (y<= 159 && y >= 136) return 5;
		else if (y<= 175 && y >= 160) return 6;
		else if (y<= 200 && y >= 176) return 7;
		else if (y<= 227 && y >= 201) return 8;
		else if (y<= 240 && y >= 228) return 9;
		else if (y<= 270 && y >= 241) return 10;
		else if (y<= 294 && y >= 271) return 11;
		else if (y<= 300 && y >= 295) return 12;
		else return -1;
	}
}
