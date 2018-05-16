package controller;

import java.util.ArrayList;

import camera.Camera;
import solver.Solver;
import utils.IntPoint;

public class AGExpert extends Thread implements ActionsGiver {
	
	public Camera camera;
	
	public Solver solver;
	
	public IntPoint lastPalet;
	
	public IntPoint position;
	
	public IntPoint lastPosition;
	
	public ArrayList<String> res;
	
	public AGExpert(Camera c) {
		camera = c;
		solver = new Solver();
		lastPosition = new IntPoint(0, 0);
	}
	
	public ArrayList<String> findGoals(IntPoint myPos) {
		if (!myPos.equals(lastPosition)) { // Le pré calcul est parti d'une mauvaise position, on recalcul
			position = myPos;
			lastPosition = null;
			lastPalet = null;
			res = null;
			run();
		}
		return (ArrayList<String>) this.res.clone();
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
			res = solver.findActions(position, listPalets);
			
			// Mise en place de la prochaine recherche
			lastPalet = paletTake(res);
			lastPosition = position;
			position = lastPos(res);
		}
	}
	
	// Supprime le palet de la liste des palet
	private void enleverPalet(ArrayList<IntPoint> listPalets, IntPoint palet) {
		for(IntPoint ip : listPalets) {
			if (ip.equals(palet)) {
				listPalets.remove(ip);
				break;
			}
		}
	}

	// Recupere la position du palet à enlever
	private IntPoint paletTake(ArrayList<String> res) {
		for(String act : res) {
			String[] s = act.split(" ");
			if (s[0].equals("prendrepalet")) {
				return new IntPoint(Integer.parseInt(s[1]), Integer.parseInt(s[2]));
			}
		}
		return null;
	}

	// Recupere la derniere position du robot
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
	
	@Override
	public void setSeekLeft(boolean bool) {
		camera.setSeekLeft(bool);
	}
	
	public AGExpert clone() {
		AGExpert age = new AGExpert(this.camera);
		age.lastPalet = new IntPoint(lastPalet.getX(), lastPalet.getY());
		age.position = new IntPoint(position.getX(), position.getY());
		age.lastPosition = new IntPoint(lastPosition.getX(), lastPosition.getY());
		return age;
	}
}
