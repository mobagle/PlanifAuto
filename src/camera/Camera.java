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

	int deltaX = 7; // X + ou - 7
	int deltaY = 10; // Y + ou - 10
	
	int[] X = new int[13];
	int[] Y = new int[13];

	// Calibre avec les 9 palets au départ
	public void calibrer(ArrayList<IntPoint> points) {
		IntPoint[] tabPoints = getTab(points); // 0,0 12,0 0,12 12,12 6,6 

		X[0] = moyenne(tabPoints[0].getX(), tabPoints[2].getX());
		X[6] = tabPoints[0].getX();
		X[12] = moyenne(tabPoints[2].getX(), tabPoints[4].getX());
		Y[0] = moyenne(tabPoints[0].getY(), tabPoints[1].getY());
		Y[6] = tabPoints[0].getY();
		Y[12] = moyenne(tabPoints[2].getY(), tabPoints[3].getY());
		
		deltaX = (X[12]-X[0])/24; // A tester, +1 peut être
		deltaY = (Y[12]-Y[0])/24; // A tester, +1 peut être
		System.out.println("deltaX : "+deltaX+" , deltaY : "+deltaY);
		
		// 0 et 12 sont déja initialisé
		for(int i=1; i<12; i++) {
			if (i!=6) {
				X[i] = X[i-1] + 2*deltaX;
				Y[i] = Y[i-1] + 2*deltaY;		
			} else {
				if (X[i] > X[i-1] + 4*deltaX) System.out.println("Probleme Calibration de X");
				if (Y[i] > Y[i-1] + 4*deltaY) System.out.println("Probleme Calibration de Y");
			}
		}
	}
	
	private int moyenne(int a, int b) {
		return (a+b)/2;
	}

	// Retourne IntPoint[] trié : 0,0 12,0 0,12 12,12 6,6 
	private IntPoint[] getTab(ArrayList<IntPoint> points) {
		IntPoint[] tab = new IntPoint[5];
		for(IntPoint ip : points) {
			if (ip.getX() > 160) {	
				if (ip.getY() < 70) {	// 12,0
					tab[1] = ip;
				} else {	// 12,12
					tab[3] = ip;
				}
			} else if (ip.getX() < 35) {
				if (ip.getY() < 70) {	// 0,0
					tab[0] = ip;
				} else {	// 0,12
					tab[2] = ip;
				}
			} else { // 6,6
				tab[4] = ip;
			}
		}
		return tab;
	}

	private boolean seekLeft;
	
	private final ArrayList<IntPoint> masterList;

	public Camera() {
		masterList = new ArrayList<IntPoint>();
		seekLeft = true;
		recupererCalibrage();
	}
	
	private void saveCalibrage() {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter("./cam.calibre"));

			String line1 = "";
			String line2 = "";
			String line3 = "";

			for(int i=0; i<13; i++) {
				line1 += X[i] + " ";
				line2 += Y[i] + " ";
			}
			line3 = deltaX + " " + deltaY;
			out.write(line1+"\n"+line2+"\n"+line3);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void recupererCalibrage() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("./cam.calibre"));
			String[] line1 = in.readLine().split(" ");
			String[] line2 = in.readLine().split(" ");
			String[] line3 = in.readLine().split(" ");
			for(int i=0; i<13; i++) {
				X[i] = Integer.parseInt(line1[i]);
				Y[i] = Integer.parseInt(line2[i]);
			}
			deltaX = Integer.parseInt(line3[0]);
			deltaY = Integer.parseInt(line3[1]);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			int valide = p.getY();
			if (valide >0 && valide<12) {
				System.out.println(" - "+p);
				masterList.add(p);
			}
		}
	}
	
	public ArrayList<IntPoint> getPaletsPositions() {
		return (ArrayList<IntPoint>) this.masterList.clone();
	}

	private IntPoint traductionPoint(IntPoint intPoint) {
		int newX = 0;
		int newY = 0;
		/*if(calibre) {*/
			newX = findX2(intPoint.getX());
			newY = findY2(intPoint.getY());
		/*} else {
			newX = findX(intPoint.getX());
			newY = findY(intPoint.getY());
		}*/
		if (!seekLeft) {
			newX = 12-newX;
			newY = 12-newY;
		}
		return new IntPoint(newX, newY);
	}
/*
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
		if (y<= 50 && y >= 43) return 0;
		else if (y<= 70 && y >= 51) return 1;
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
	*/
	private int findX2(int x) {
		for(int i=1;i<12;i++) {
			if (x > X[i]-deltaX && x <= X[i]+deltaX)
				return i;
		}
		if (x > 200 || x < 0) return -1;	// Valeurs maxs
		else if (x < 80) return 0;
		else return 12;
	}

	private int findY2(int y) {
		for(int i=1;i<12;i++) {
			if (y > Y[i]-deltaY && y <= Y[i]+deltaY)
				return i;
		}
		if (y > 300 || y < 40) return -1;	// Camp adverse ou notre camp
		else if (y < 150) return 0;
		else return 12;
	}
}
