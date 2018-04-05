package camera;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import utils.IntPoint;

public class Traducteur {

	private float deltaX = 200/24;
	private float deltaY = 300/24;
	
	private float[] X = new float[13];
	private float[] Y = new float[13];

	private boolean seekLeft;

	private final String filename = "./cam.calibre";
	
	Traducteur() {
		recupererCalibrage();
	}
	
	public void setSeekLeft(boolean b) {
		seekLeft = b;
		//System.out.println("seekLeft : "+seekLeft);
	}
	
	private void recupererCalibrage() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(filename));
			String[] line1 = in.readLine().split(" ");
			String[] line2 = in.readLine().split(" ");
			for(int i=0; i<13; i++) {
				X[i] = Float.valueOf(line1[i]);
				Y[i] = Float.valueOf(line2[i]);
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveCalibrage() {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(filename));
			String line1 = "";
			String line2 = "";
			for(int i=0; i<13; i++) {
				line1 += X[i] + " ";
				line2 += Y[i] + " ";
			}
			out.write(line1+"\n"+line2);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private float moyenne(float a, float b) {
		return (a+b)/2;
	}
	
	public void calibrer(ArrayList<IntPoint> lastPointsReceived) {
		IntPoint[] tabPoints = getTab(lastPointsReceived); // {0,0; 12,0; 0,12; 12,12; 6,6} 
		X[0] = moyenne(tabPoints[0].getX(), tabPoints[2].getX());
		X[6] = tabPoints[4].getX();
		X[12] = moyenne(tabPoints[1].getX(), tabPoints[3].getX());
		Y[0] = moyenne(tabPoints[0].getY(), tabPoints[1].getY());
		Y[6] = tabPoints[4].getY();
		Y[12] = moyenne(tabPoints[2].getY(), tabPoints[3].getY());

		// 0 et 12 sont déja initialisé
		for(int i=1; i<12; i++) {
			if (i != 6) {
				X[i] = moyenne(X[0] + i*2*deltaX, X[12] - (24-(i*2))*deltaX);
				Y[i] = moyenne(Y[0] + i*2*deltaY, Y[12] - (24-(i*2))*deltaY);
				System.out.println("X["+i+"] = "+X[i]+" ,Y["+i+"] = "+Y[i]);
			} else {
				if (X[i] > X[i-1] + 2.5*deltaX) System.out.println("TRADUCTEUR : Probleme Calibration de X");
				if (Y[i] > Y[i-1] + 2.5*deltaY) System.out.println("TRADUCTEUR : Probleme Calibration de Y");
			}
		}
		saveCalibrage();
	}

	// Retourne IntPoint[] trié : 0,0 12,0 0,12 12,12 6,6 
	private IntPoint[] getTab(ArrayList<IntPoint> points) {
		IntPoint[] tab = new IntPoint[5];
		for(IntPoint ip : points) {
			if (ip.getX() > 160) {	
				if (ip.getY() < 70) {		// 12,0
					tab[1] = ip;
				} else {					// 12,12
					tab[3] = ip;
				}
			} else if (ip.getX() < 35) {
				if (ip.getY() < 70) {		// 0,0
					tab[0] = ip;
				} else {					// 0,12
					tab[2] = ip;
				}
			} else {						// 6,6
				tab[4] = ip;
			}
		}
		return tab;
	}

	public ArrayList<IntPoint> traduire(ArrayList<IntPoint> lastPointsReceived) {
		ArrayList<IntPoint> liste = new ArrayList<>();
		for (IntPoint ip : lastPointsReceived)
			liste.add(traductionPoint(ip));
		return liste;
	}

	private IntPoint traductionPoint(IntPoint intPoint) {
		int newX = 12-findX(intPoint.getX()); // -12 Pour que x = 0 soit toujours à notre droite
		int newY = findY(intPoint.getY());
		if (!seekLeft && newX!=-1 && newY!=-1) {
			newX = 12-newX;
			newY = 12-newY;
		}
		return new IntPoint(newX, newY);
	}
	
	private int findX(int x) {
		// Regle un de déformation de la camera
		if (x > X[9]-(2*deltaX) && x <= X[9]+(2*deltaX)) return 9;
		for(int i=0;i<13;i++) {
			if (x > X[i]-deltaX && x <= X[i]+deltaX)
				return i;
		}
		return -1;
	}

	private int findY(int y) {
		// Regle un de déformation de la camera
		if (y > Y[9]-(2*deltaY) && y <= Y[9]+(2*deltaY)) return 9;
		for(int i=0;i<13;i++) {
			if (y > Y[i]-deltaY && y <= Y[i]+deltaY)
				return i;
		}
		return -1;
	}
}
