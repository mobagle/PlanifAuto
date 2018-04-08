package controller;

import java.util.ArrayList;

import camera.Camera;
import solver.Solver;
import utils.IntPoint;

public class AGExpert extends Thread implements ActionsGiver {
	
	private Camera camera;
	
	private Solver s;
	
	private IntPoint lastPalet;
	
	private IntPoint lastPosition;
	
	private ArrayList<String> res;
	
	public AGExpert(Camera c) {
		camera = c;
		s = new Solver();
	}
	
	public ArrayList<String> findGoals(IntPoint myPos) {
		if (!myPos.equals(lastPosition)) { // Le pré calcul est parti d'une mauvaise position, on recalcul
			lastPosition = myPos;
			lastPalet = null;
			res = null;
			run();
		}
		ArrayList<String> resultat = (ArrayList<String>) this.res.clone();
		this.start();
		return resultat;
	}

	
	/**
	 * Lance pdll pour trouver les actions à effectuer
	 */
	public void run() {
		// Recuperation des points
		ArrayList<IntPoint> listPalets = camera.getPaletsPositions();
		if (listPalets.size() == 0) {	// Plus de palet sur la table
			res = null;
			lastPalet = null;
			lastPosition = null;
		} else {
			// Supprime le palet en cours de déplacement
			if (lastPalet != null) enleverPalet(listPalets, lastPalet);
			
			// Recherche des action a effectuer
			res = s.findActions(lastPosition, listPalets);
			lastPalet = paletTake(res);	// Mise en place de la prochaine recherche
			lastPosition = lastPos(res);
		}
	}

	private void enleverPalet(ArrayList<IntPoint> listPalets, IntPoint palet) {
		for(IntPoint ip : listPalets) {
			if (ip.equals(palet)) {
				listPalets.remove(ip);
				break;
			}
		}
	}

	private IntPoint paletTake(ArrayList<String> res) {
		for(String act : res) {
			String[] s = act.split(" ");
			if (s[0].equals("prendrepalet")) {
				return new IntPoint(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
			}
		}
		return null;
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
