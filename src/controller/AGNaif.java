package controller;

import java.util.ArrayList;

import camera.Camera;
import solver.Solver;
import utils.IntPoint;

public class AGNaif implements ActionsGiver{

	private Camera camera;
	
	private Solver s;
		
	public AGNaif(Camera c) {
		camera = c;
		s = new Solver();
	}
	
	public ArrayList<String> findGoals(IntPoint myPos) {
		// Recuperation des points
		ArrayList<IntPoint> listPalets = camera.getPaletsPositions();
		
		if (listPalets.size() == 0) return null;

		// Recherche des action a effectuer
		ArrayList<String> res = s.findActions(myPos, listPalets);
		return res;
	}
}
