package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.ListIterator;

import camera.Camera;
import motors.Graber;
import motors.TImedMotor;
import motors.Propulsion;
import sensors.ColorSensor;
import sensors.PressionSensor;
import sensors.VisionSensor;
import vue.InputHandler;
import vue.Screen;
import solver.Solver;
import utils.IntPoint;
import utils.R2D2Constants;
import lejos.hardware.Button;
import lejos.robotics.Color;

public class Controler {

	protected ColorSensor color = null;
	protected Propulsion propulsion = null;
	protected Graber graber = null;
	protected PressionSensor pression = null;
	protected VisionSensor vision = null;
	protected Screen screen = null;
	protected InputHandler input = null;
	protected Camera camera = null;
	public static boolean seekLeft;
	private IntPoint myPos;
	private IntPoint dest;
	private double distanceTot;
	private long runningTimeTot;
	private long timeForOneUnit;

	enum State {
		toThePalet, toTheHome
	};

	private ArrayList<TImedMotor> motors = new ArrayList<TImedMotor>();

	public Controler(Camera cam) {
		propulsion = new Propulsion();
		graber = new Graber();
		color = new ColorSensor();
		pression = new PressionSensor();
		vision = new VisionSensor();
		screen = new Screen();
		camera = cam;
		input = new InputHandler(screen);
		motors.add(propulsion);
		motors.add(graber);
		myPos = new IntPoint(0, 0);
		distanceTot = 0;
		runningTimeTot = 0;
		timeForOneUnit = 2500;
	}

	/**
	 * Lance le robot. Dans un premier temps, effectue une calibration des capteurs.
	 * Dans un second temps, lance des tests Dans un troisième temps, démarre la
	 * boucle principale du robot pour la persycup
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void start() throws IOException, ClassNotFoundException {
		loadCalibration();

		screen.drawText("Calibration", "Appuyez sur echap ", "pour skipper");
		boolean skip = input.waitOkEscape(Button.ID_ESCAPE);
		if (skip || calibration()) {
			if (!skip) {
				saveCalibration();
			}
			screen.drawText("Lancer", "Appuyez sur OK si la", "ligne noire est à gauche", "Appuyez sur tout autre",
					"elle est à droite");
			if (input.isThisButtonPressed(input.waitAny(), Button.ID_ENTER)) {
				seekLeft = true;

			} else {
				seekLeft = false;
			}
			mainLoop();
		}

		// my code
		// seekLeft = true;
		// mainLoop();
		// end my code

		cleanUp();
	}

	/**
	 * Charge la calibration du fichier de configuration si elle existe
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void loadCalibration() throws FileNotFoundException, IOException, ClassNotFoundException {
		File file = new File("calibration");
		if (file.exists()) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			color.setCalibration((float[][]) ois.readObject());
			graber.setOpenTime((long) ois.readObject());
			ois.close();
		}
	}

	/**
	 * Sauvegarde la calibration
	 * 
	 * @throws IOException
	 */
	private void saveCalibration() throws IOException {
		screen.drawText("Sauvegarde", "Appuyez sur le bouton central ", "pour valider id", "Echap pour ne pas sauver");
		if (input.waitOkEscape(Button.ID_ENTER)) {
			File file = new File("calibration");
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
			ObjectOutputStream str = new ObjectOutputStream(new FileOutputStream(file));
			str.writeObject(color.getCalibration());
			str.writeObject(graber.getOpenTime());
			str.flush();
			str.close();
		}
	}

	/**
	 * Effectue l'ensemble des actions nécessaires à l'extinction du programme
	 */
	private void cleanUp() {
		if (!graber.isOpen()) {
			graber.open();
			while (graber.isRunning()) {
				graber.checkState();
			}
		}
		propulsion.runFor(500, true);
		while (propulsion.isRunning()) {
			propulsion.checkState();
		}
		color.lightOff();
	}

	/**
	 * Lance les tests du robot, peut être desactivé pour la persy cup
	 */
	@SuppressWarnings("unused")
	private void runTests() {
		SystemTest.grabberTest(this);
	}

	/**
	 * Lance pdll pour trouver les actions à effectuer
	 */
	private static ArrayList<String> findGoals() {
		Solver s = new Solver();

		// R�cuperation des points
		// camera.get ...
		// Pour l'instant statics
		IntPoint marvin = new IntPoint(0, 0);
		ArrayList<IntPoint> listPalets = new ArrayList<>();
		for (int i = 3; i < 10; i = i + 3)
			for (int j = 3; j < 10; j = j + 3)
				listPalets.add(new IntPoint(i, j));
		// listPalets.add(new IntPoint(3, 3));

		// Recherche des action a effectuer
		return s.findActions(marvin, null, listPalets);
	}

	private boolean executeold(String ac) {
		// X largeur du terrain, Y longueur
		System.out.println(ac);
		String action[] = ac.split(" ");
		int x0, x1, y0, y1, distance;
		for (TImedMotor m : motors)
			m.checkState();
		switch (action[0]) {
		case "prendrePalet":
			// avance jusqu'� toucher le palet
			propulsion.runFor(R2D2Constants.MAX_GRABING_TIME, true);
			while (vision.getRaw()[0] > R2D2Constants.COLLISION_DISTANCE && !pression.isPressed()) {
				// si pas trouv� de palet apr�s un certain temps, sort
				if (!propulsion.isRunning() || input.escapePressed())
					return false;
				propulsion.checkState();
			}
			// arret des roues
			propulsion.stopMoving();
			// ferme les pince
			graber.close();
			// attend la fin de la fermeture des pinces
			while (graber.isRunning()) {
				graber.checkState();
				if (input.escapePressed())
					return false;
			}
			return true;

		case "lacherPalet":
			// ouvre les pinces
			graber.open();
			// attend la fin de l'ouverture des pinces
			while (graber.isRunning())
				;
			// recule apr�s avoir lacher le palet
			propulsion.runFor(R2D2Constants.EMPTY_HANDED_STEP_FORWARD, false);
			while (propulsion.isRunning()) {
				propulsion.checkState();
				if (input.escapePressed())
					return false;
			}
			return true;

		case "deplacementX1": // vers un x plus grand
			x0 = Integer.valueOf(action[1]);
			x1 = Integer.valueOf(action[2]);
			distance = x1 - x0;
			// rotation
			propulsion.orientateNorth();
			while (propulsion.isRunning()) {
				propulsion.checkState();
			}
			// d�placement
			propulsion.runFor(R2D2Constants.THREE_QUARTER_S, true);
			while (propulsion.isRunning()) {
				propulsion.checkState();
				if (input.escapePressed())
					return false;
			}
			return true;
		case "deplacementX2": // vers un x plus petit
			x0 = Integer.valueOf(action[1]);
			x1 = Integer.valueOf(action[2]);
			distance = x0 - x1;
			// rotation
			propulsion.orientateSouth(seekLeft);
			while (propulsion.isRunning()) {
				propulsion.checkState();
			}
			// d�placement
			propulsion.runFor(R2D2Constants.THREE_QUARTER_S, true);
			while (propulsion.isRunning()) {
				propulsion.checkState();
				if (input.escapePressed())
					return false;
			}
			return true;

		case "deplacementY1": // vers un y plus grand
			y0 = Integer.valueOf(action[1]);
			y1 = Integer.valueOf(action[2]);
			distance = y1 - y0;
			// rotation
			propulsion.orientateEast();
			while (propulsion.isRunning()) {
				propulsion.checkState();
			}
			// d�placement
			propulsion.runFor(R2D2Constants.THREE_QUARTER_S, true);
			while (propulsion.isRunning()) {
				propulsion.checkState();
				if (input.escapePressed())
					return false;
			}
			return true;

		case "deplacementY2": // vers un y plus petit
			y0 = Integer.valueOf(action[1]);
			y1 = Integer.valueOf(action[2]);
			distance = y0 - y1;
			// rotation
			propulsion.orientateWest();
			while (propulsion.isRunning()) {
				propulsion.checkState();
			}
			// d�placement
			propulsion.runFor(R2D2Constants.THREE_QUARTER_S, true);
			while (propulsion.isRunning()) {
				propulsion.checkState();
				if (input.escapePressed())
					return false;
			}
			return true;

		default:
			return false;
		}
	}
	
	private void majTimeToRunByUnit(double dist, long time) {
		System.out.println("Old TimeToRunByUnit: " + timeForOneUnit +" ms | dist: "+ dist+" | time: "+time);
		distanceTot += dist;
		runningTimeTot +=time;
		timeForOneUnit = (long) (runningTimeTot/distanceTot);
		System.out.println("New TimeToRunByUnit: " + timeForOneUnit +" ms");
	}

	private boolean execute(String ac) {
		// X largeur du terrain, Y longueur
		screen.drawText("Action", ac);
		System.out.println(ac);
		String action[] = ac.split(" ");
		int x0, x1, y0, y1;
		for (TImedMotor m : motors)
			m.checkState();
		switch (action[0]) {
		case "prendrePalet":
			boolean ret = false;
			double distance = distance(myPos, dest);
			int ttl = (int)(timeForOneUnit * distance);
			// avance jusqu'� toucher le palet
			propulsion.runFor(ttl,true);
			long start = System.currentTimeMillis();
			long elapsedTime = 0;
			while (propulsion.isRunning()) {
				propulsion.checkState();
				/*
				 * if(vision.getRaw()[0] <= R2D2Constants.COLLISION_DISTANCE) { // arret des
				 * roues propulsion.stopMoving(); System.out.println("COLLISION"); return false;
				 * } else
				 */
				if (pression.isPressed()) {
					// arret des roues
					System.out.println("PRESSION");
					propulsion.stopMoving();
					elapsedTime = System.currentTimeMillis() - start;
					majTimeToRunByUnit(distance, elapsedTime);
					ret = true;
					break;
				}
			}
			graber.close();
			// attend la fin de la fermeture des pinces
			while (graber.isRunning()) {
				graber.checkState();
			}
			return ret;

		case "lacherPalet":
			distance = distance(myPos, dest);
			ttl = (int)(timeForOneUnit * distance);
			// avance jusqu'� toucher le palet
			propulsion.runFor(ttl,true);
			while (propulsion.isRunning()) {
				propulsion.checkState();
			}
			// ouvre les pinces
			graber.open();
			// attend la fin de l'ouverture des pinces
			while (graber.isRunning()) {
				graber.checkState();
			}

			// recule apr�s avoir lacher le palet
			propulsion.runFor(R2D2Constants.EMPTY_HANDED_STEP_FORWARD, false);
			while (propulsion.isRunning()) {
				propulsion.checkState();
			}
			return true;

		case "deplacement":
			//initialisation des points
			x0 = Integer.valueOf(action[1]);
			x1 = Integer.valueOf(action[2]);
			y0 = Integer.valueOf(action[3]);
			y1 = Integer.valueOf(action[4]);
			myPos = new IntPoint(x0, y0);
			dest = new IntPoint(x1, y1);
			IntPoint north = new IntPoint(x0, 12);
			// rotation

			int degrees = angle(myPos, dest, north);
			System.out.println("angle calcul: " + degrees);
			
			int myOrientation = (int)propulsion.getRotateToNorth();
			System.out.println("mon orientation: " + myOrientation);
			
			int angle = degrees - myOrientation;
			System.out.println("angle corrected: " + angle);

			propulsion.rotate(Math.abs(angle), false, true);
			while (propulsion.isRunning()) {
				propulsion.checkState();
			}

			return true;

		default:
			return false;
		}
	}

	/** retourne la distance absolue entreles points p1 et p2 */
	private double distance(IntPoint p1, IntPoint p2) {
		return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
	}

	/** retourne l'angle en degrees en p1 form� par les points p2 et p3*/
	private int angle(IntPoint p1, IntPoint p2, IntPoint p3) {
		double ab = distance(p1, p2);
		double ac = distance(p1, p3);
		double bc = distance(p3, p2);
		double rad = Math.acos(((Math.pow(ab, 2) + Math.pow(ac, 2) - Math.pow(bc, 2)) / (2 * ab * ac)));
		Double degree = (rad * 180) / Math.PI;
		return degree.intValue();
	}

	private void mainLoop() {

		boolean run = true;
		boolean pasDeProbleme = true;
		ArrayList<String> goals = new ArrayList<>();
		String goal;

		goals.add("deplacement 0 2 0 3");
		goals.add("prendrePalet");
		goals.add("deplacement 2 0 3 0");
		goals.add("lacherPalet");
		goals.add("deplacement 0 0 0 3");
		goals.add("prendrePalet");
		goals.add("deplacement 0 0 3 0");
		goals.add("lacherPalet");

		ListIterator<String> li = goals.listIterator();
		while (li.hasNext() && pasDeProbleme) {
			goal = li.next();
			if (goal != null) {
				pasDeProbleme = execute(goal);
			}
		}

		/**
		 * real loop after while(run){ goals = findGoals(); if(goals != null){
		 * ListIterator<String> li = goals.listIterator(); while (li.hasNext() &&
		 * pasDeProbleme) { goal = li.next(); if(goal != null){ pasDeProbleme =
		 * execute(goal); } } } else { run = false; }
		 * 
		 * }
		 */
	}

	/**
	 * S'occupe d'effectuer l'ensemble des calibrations nécessaires au bon
	 * fonctionnement du robot.
	 * 
	 * @return vrai si tout c'est bien passé.
	 */
	private boolean calibration() {
		return calibrationGrabber() && calibrationCouleur();
	}

	private boolean calibrationGrabber() {
		screen.drawText("Calibration", "Calibration de la fermeture de la pince", "Appuyez sur le bouton central ",
				"pour continuer");
		if (input.waitOkEscape(Button.ID_ENTER)) {
			screen.drawText("Calibration", "Appuyez sur ok", "pour lancer et arrêter");
			input.waitAny();
			graber.startCalibrate(false);
			input.waitAny();
			graber.stopCalibrate(false);
			screen.drawText("Calibration", "Appuyer sur Entree", "pour commencer la", "calibration de l'ouverture");
			input.waitAny();
			screen.drawText("Calibration", "Appuyer sur Entree", "Quand la pince est ouverte");
			graber.startCalibrate(true);
			input.waitAny();
			graber.stopCalibrate(true);

		} else {
			return false;
		}
		return true;
	}

	/**
	 * Effectue la calibration de la couleur
	 * 
	 * @return renvoie vrai si tout c'est bien passé
	 */
	private boolean calibrationCouleur() {
		screen.drawText("Calibration", "Préparez le robot à la ", "calibration des couleurs",
				"Appuyez sur le bouton central ", "pour continuer");
		if (input.waitOkEscape(Button.ID_ENTER)) {
			color.lightOn();

			// calibration gris
			screen.drawText("Gris", "Placer le robot sur ", "la couleur grise");
			input.waitAny();
			color.calibrateColor(Color.GRAY);

			// calibration rouge
			screen.drawText("Rouge", "Placer le robot ", "sur la couleur rouge");
			input.waitAny();
			color.calibrateColor(Color.RED);

			// calibration noir
			screen.drawText("Noir", "Placer le robot ", "sur la couleur noir");
			input.waitAny();
			color.calibrateColor(Color.BLACK);

			// calibration jaune
			screen.drawText("Jaune", "Placer le robot sur ", "la couleur jaune");
			input.waitAny();
			color.calibrateColor(Color.YELLOW);

			// calibration bleue
			screen.drawText("BLeue", "Placer le robot sur ", "la couleur bleue");
			input.waitAny();
			color.calibrateColor(Color.BLUE);

			// calibration vert
			screen.drawText("Vert", "Placer le robot ", "sur la couleur vert");
			input.waitAny();
			color.calibrateColor(Color.GREEN);

			// calibration blanc
			screen.drawText("Blanc", "Placer le robot ", "sur la couleur blanc");
			input.waitAny();
			color.calibrateColor(Color.WHITE);

			color.lightOff();
			return true;
		}
		return false;
	}
}
