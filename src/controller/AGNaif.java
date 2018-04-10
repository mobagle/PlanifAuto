package controller;

import java.util.ArrayList;

import camera.Camera;
import solver.Solver;
import utils.IntPoint;

public class AGNaif implements ActionsGiver{

	private Camera camera;
	
	private Solver solver;
		
	public AGNaif(Camera c) {
		camera = c;
		solver = new Solver();
	}
	
	public ArrayList<String> findGoals(IntPoint myPos) {
		// Recuperation des points
		ArrayList<IntPoint> listPalets = camera.getPaletsPositions();
		
		if (listPalets.size() == 0) return null;

		// Recherche des action a effectuer
		return solver.findActions(myPos, listPalets);
	}

	@Override
	public void setSeekLeft(boolean bool) {
		camera.setSeekLeft(bool);
	}
}
