package controller;

import camera.Camera;

public class FactoryAG {

	private boolean expert = false;
	
	public FactoryAG(boolean rapide) {
		expert = rapide;
	}
	
	// Crée l'actionGiver en fonction du boolean
	public ActionsGiver createActionGiver(Camera cam) {
		if (expert) return new AGExpert(cam);
		else return new AGNaif(cam);
	}
}
