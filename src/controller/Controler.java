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
	protected ActionsGiver actionsGiver = null;
	public static boolean seekLeft;
	private IntPoint myPos;
	private IntPoint dest;
	private double distanceTot;
	private long runningTimeTot;
	private long timeForOneUnit;
	private long start;
	private long elapsedTime;
	private double distance;
	private boolean vaPoser;
	private boolean firstPass;
	private boolean aGauche;
	private boolean touche;

	private ArrayList<TImedMotor> motors = new ArrayList<TImedMotor>();

	public Controler(Camera cam) {
		propulsion = new Propulsion();
		graber = new Graber();
		color = new ColorSensor();
		pression = new PressionSensor();
		vision = new VisionSensor();
		screen = new Screen();
		FactoryAG fag = new FactoryAG(false);
		actionsGiver = fag.createActionGiver(cam);
		input = new InputHandler(screen);
		motors.add(propulsion);
		motors.add(graber);
		distanceTot = 0;
		runningTimeTot = 0;
		timeForOneUnit = 2000;
		vaPoser = false;
		firstPass = true;
		touche = false;
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
		boolean cal = loadCalibration();

		screen.drawText("Calibration", "Appuyez sur echap ", "pour skipper");
		boolean skip = cal || input.waitOkEscape(Button.ID_ESCAPE);
		if (skip || calibration()) {
			if (!skip) {
				saveCalibration();
			}
			seekLeft = true;

			screen.drawText("Lancer", "Appuyez sur OK si la", "ligne noire est a� gauche", "Appuyez sur tout autre",
					"elle est a droite");
			if (input.isThisButtonPressed(input.waitAny(), Button.ID_ENTER)) {
				seekLeft = true;
			} else {
				seekLeft = false;
			}

			actionsGiver.setSeekLeft(seekLeft);
			screen.clearPrintln();
			screen.clearDraw();
			screen.drawText("Position", "< Gauche", "OK Milieu", "> Droite");
			int btn = input.waitAny();
			if (btn == Button.ID_LEFT) {
				myPos = new IntPoint(3, 0);
			} else if (btn == Button.ID_RIGHT) {
				myPos = new IntPoint(9, 0);
			} else if (btn == Button.ID_ENTER) {
				myPos = new IntPoint(6, 0);
			}
			if (myPos != null) {
				mainLoop();
			}
		}
		cleanUp();
	}

	/**
	 * Charge la calibration du fichier de configuration si elle existe
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private boolean loadCalibration() throws FileNotFoundException, IOException, ClassNotFoundException {
		File file = new File("calibration");
		if (file.exists()) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			color.setCalibration((float[][]) ois.readObject());
			graber.setOpenTime((long) ois.readObject());
			ois.close();
			return true;
		}
		return false;
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

	/** Mets � jour le temps moyen n�cessaire pour avancer d'une unit� */
	private void majTimeToRunByUnit(double dist, long time) {
		System.out.println("Old TimeToRunByUnit: " + timeForOneUnit + " ms | dist: " + dist + " | time: " + time);
		distanceTot += dist;
		runningTimeTot += time;
		timeForOneUnit = (long) (runningTimeTot / distanceTot);
		System.out.println("New TimeToRunByUnit: " + timeForOneUnit + " ms");
	}

	private boolean execute(String ac) {
		// X largeur du terrain, Y longueur
		// System.out.println(ac);
		String action[] = ac.split(" ");
		int x0, x1, y0, y1;
		for (TImedMotor m : motors)
			m.checkState();
		switch (action[0]) {
		case "prendrepalet":
			/*
			 * 
			 * if(vision.getRaw()[0] <= R2D2Constants.COLLISION_DISTANCE) { // arret des
			 * roues propulsion.stopMoving(); System.out.println("COLLISION"); return false;
			 * }
			 */

			if (!touche) {
				propulsion.runFor(800, true);
				while (propulsion.isRunning()) {
					if (pression.isPressed()) {
						propulsion.stopMoving();
						break;
					}
					propulsion.checkState();
				}
			}

			elapsedTime = System.currentTimeMillis() - start;
			majTimeToRunByUnit(distance, elapsedTime);
			graber.close();
			// attend la fin de la fermeture des pinces
			while (graber.isRunning()) {
				graber.checkState();
			}
			vaPoser = true;
			return true;

		case "lacherpalet":

			propulsion.runFor(400, true);
			while (propulsion.isRunning()) {
				propulsion.checkState();
			}
			// ouvre les pinces
			graber.open();
			// attend la fin de l'ouverture des pinces
			while (graber.isRunning()) {
				graber.checkState();
			}

			// recule apres avoir lacher le palet
			propulsion.runFor(400, false);
			while (propulsion.isRunning()) {
				propulsion.checkState();
			}
			/*
			 * propulsion.rotate(180, true, false); while (propulsion.isRunning()) {
			 * propulsion.checkState(); }
			 */

			vaPoser = false;
			firstPass = false;
			return true;

		case "deplacement":
			touche = false;
			// initialisation des points
			x0 = Integer.valueOf(action[1]);
			y0 = Integer.valueOf(action[2]);
			x1 = Integer.valueOf(action[3]);
			y1 = Integer.valueOf(action[4]);
			myPos = new IntPoint(x0, y0);
			dest = new IntPoint(x1, y1);
			IntPoint north = new IntPoint(x0, 16);
			// rotation

			int degrees = angle(myPos, dest, north);
			// System.out.println("computed angle: " + degrees);

			int myOrientation = (int) propulsion.getRotateToNorth();
			// System.out.println("my orientation: " + myOrientation);

			int angle = degrees + myOrientation;
			angle = angle % 360;
			if (angle > 180)
				angle -= 360;

			if (!vaPoser && !firstPass) {
				if (x0 > x1) {
					angle = -angle;
					aGauche = true;
				} else {
					aGauche = false;
				}
			} else if (vaPoser && !firstPass && aGauche) {
				angle = Math.abs(angle);
			}
			// System.out.println("angle corrected: " + angle);

			boolean left = false;
			if (angle < 0)
				left = true;

			propulsion.rotate(Math.abs(angle)+2, left, true);
			while (propulsion.isRunning()) {
				propulsion.checkState();
			}

			distance = distance(myPos, dest);
			int ttl = (int) (timeForOneUnit * distance);
			System.out.println("distance: " + distance + " | timeByUnit: " + timeForOneUnit + " | ttl: " + ttl);
			start = System.currentTimeMillis();

			// si le robot doit ramener le palet
			if (vaPoser) {
				avoidObstruct();
				return true;
				// si le robot va chercher un palet
			} else {
				propulsion.runFor(ttl, true);
			}
			myPos = dest;

			while (propulsion.isRunning()) {
				propulsion.checkState();
				// conditions d'arret des roues
				if ((!vaPoser && pression.isPressed()) || (vaPoser && color.getCurrentColor() == Color.WHITE)) {
					touche = true;
					propulsion.stopMoving();
					return true;
				}
			}
			return true;
		default:
			return false;
		}
	}

	/**
	 * permet au robot de poser le palet en prennant une diagonale au lieu d'aller
	 * tout droit dans le cas ou il y a d'autres palets sur l'axe x le mennant au
	 * camp adverse
	 */
	private void avoidObstruct() {
		// calcul de la nouvelle destination
		if (dest.getX() > 3) {
			dest.setX(dest.getX() - 3);
		} else {
			dest.setX(6);
		}

		// calcul de l'angle pour atteindre la nouvelle destination
		IntPoint north = new IntPoint(myPos.getX(), 16);
		int degrees = angle(myPos, dest, north);
		int myOrientation = (int) propulsion.getRotateToNorth();
		int angle = degrees + myOrientation;
		angle = angle % 360;
		if (angle > 180) angle -= 360;
		if (myPos.getX() < dest.getX()) {
			angle = -angle;
		}
		boolean left = false;
		if (angle < 0)
			left = true;

		//ex�cute une roation pour se positionner face � sa destination
		propulsion.rotate(Math.abs(angle)+2, left, true);
		while (propulsion.isRunning()) {
			propulsion.checkState();
		}

		//avance tout droit jusqu'� trouver la ligne blanche 
		propulsion.run(true);
		while (propulsion.isRunning()) {
			propulsion.checkState();
			if (color.getCurrentColor() == Color.WHITE) {
				propulsion.stopMoving();
			}
		}
		
		//se repositionne face au camp adverse
		propulsion.rotate(Math.abs(angle)+2, !left, true);
		while (propulsion.isRunning()) {
			propulsion.checkState();
		}

		//mets � jour la position actuelle
		myPos = dest;

	}

	/** retourne la distance absolue entre les points p1 et p2 */
	private double distanceBasic(IntPoint p1, IntPoint p2) {
		return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
	}

	/**
	 * retourne la distance absolue entre les points p1 et p2 dans la grille pas
	 * carre (50cm * 60cm)
	 */
	private double distance(IntPoint p1, IntPoint p2) {
		double etirementY = 1.2;
		return Math.sqrt(
				Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() * etirementY - p1.getY() * etirementY, 2));
	}

	/** retourne l'angle en degrees en p1 form� par les points p2 et p3 */
	private int angle(IntPoint p1, IntPoint p2, IntPoint p3) {
		double ab = distance(p1, p2);
		double ac = distance(p1, p3);
		double bc = distance(p3, p2);
		double rad = Math.acos(((Math.pow(ab, 2) + Math.pow(ac, 2) - Math.pow(bc, 2)) / (2 * ab * ac)));
		double degree = (rad * 180) / Math.PI;
		return (int) Math.round(degree);
	}

	private void mainLoop() {
		boolean run = true;
		boolean pasDeProbleme = true;
		propulsion.seDegreeToNorth(0);

		screen.clearPrintln();
		screen.clearDraw();
		screen.drawText("Preparation !!");
		ArrayList<String> goals = actionsGiver.findGoals(this.myPos);
		
		screen.clearPrintln();
		screen.clearDraw();
		screen.drawText("Let's go ?");
		input.waitAny();
		
		while (run) {
			if (goals == null)
				run = false;
			else {
				for (String goal : goals) {
					screen.drawText("Action", goal);
					System.out.println(goal);
					if (goal != null) {
						pasDeProbleme = execute(goal);
					}
				}
				screen.clearPrintln();
				screen.clearDraw();
				screen.drawText("Reflexion", "Calcul de l'itineraire", "en cours");
				goals = actionsGiver.findGoals(this.myPos);
			}
		}
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
