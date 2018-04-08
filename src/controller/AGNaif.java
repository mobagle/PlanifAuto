package controller;

import java.util.ArrayList;

import camera.Camera;
import solver.Solver;
import utils.IntPoint;

public class AGNaif implements ActionsGiver{

	private Camera camera;
	
	private Solver s;
	
	private IntPoint lastPosition;
	
	public AGNaif(Camera c) {
		camera = c;
		s = new Solver();
	}
	
	public ArrayList<String> findGoals(IntPoint myPos) {
		lastPosition = myPos;
		return findGoals();
	}
	
	public ArrayList<String> findGoals() {

		// Recuperation des points
		ArrayList<IntPoint> listPalets = camera.getPaletsPositions();
		
		if (listPalets.size() == 0) return null;

		// Recherche des action a effectuer
		ArrayList<String> res = s.findActions(lastPosition, listPalets);
		lastPosition = lastPos(res);
		return res;
	}
	
	private IntPoint lastPos(ArrayList<String> res) {
		int nbAction = res.size();
		String act = res.get(nbAction-1);
		String[] s = act.split(" ");

		if (s[0].equals("lacherpalet")) {
			return new IntPoint(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
		} else {
			System.out.println("PROBLEME ActionGiver recuperer lastPosition");
			return null;
		}
	}
}
