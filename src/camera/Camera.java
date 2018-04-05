package camera;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import utils.IntPoint;

public class Camera {

	private final ArrayList<IntPoint> masterList;

	private Traducteur traducteur;
	
	public Camera() {
		masterList = new ArrayList<IntPoint>();
		traducteur = new Traducteur();
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
		for(IntPoint tp : traducteur.traduire(PointsList)){
			int valide = tp.getY();
			System.out.println(" - "+tp);
			if (valide >0 && valide<12) {
				masterList.add(tp);
			}
		}
	}
	
	public ArrayList<IntPoint> getPaletsPositions() {
		return (ArrayList<IntPoint>) this.masterList.clone();
	}

	public void setSeekLeft(boolean seekLeft) {
		traducteur.setSeekLeft(seekLeft);		
	}

	public void calibrer(ArrayList<IntPoint> lastPointsReceived) {
		traducteur.calibrer(lastPointsReceived);		
	}

}
