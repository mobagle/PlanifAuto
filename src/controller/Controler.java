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
	protected ActionsGiver actionGiver = null;

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
	private boolean ag;

	private ArrayList<TImedMotor> motors = new ArrayList<TImedMotor>();

	public Controler(Camera cam, boolean AGExpert) {
		propulsion = new Propulsion();
		graber = new Graber();
		color = new ColorSensor();
		pression = new PressionSensor();
		vision = new VisionSensor();
		screen = new Screen();
		ag = AGExpert;
		FactoryAG fag = new FactoryAG(AGExpert);
		actionGiver = fag.createActionGiver(cam);
		camera = cam;
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
	 * Dans un second temps, lance des tests Dans un troisieme temps, demarre la
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

			screen.drawText("Lancer", "Appuyez sur OK si la", "camera est a gauche", "Appuyez sur tout autre",
					"elle est a droite");
			if (input.isThisButtonPressed(input.waitAny(), Button.ID_ENTER)) {
				seekLeft = false;
			} else {
				seekLeft = true;
			}

			camera.setSeekLeft(seekLeft);
			screen.clearPrintln();
			screen.clearDraw();
			screen.drawText("Position", "< Gauche", "OK Milieu", "> Droite");
			int btn = input.waitAny();
			if (btn == Button.ID_LEFT) {
				myPos = new IntPoint(9, 0);
			} else if (btn == Button.ID_RIGHT) {
				myPos = new IntPoint(3, 0);
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
	 * Effectue l'ensemble des actions necessaires a� l'extinction du programme
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
	 * Lance les tests du robot, peut etre desactive pour la persy cup
	 */
	@SuppressWarnings("unused")
	private void runTests() {
		SystemTest.grabberTest(this);
	}

	/** Mets a jour le temps moyen necessaire pour avancer d'une "unite" dans notre grille */
	private void majTimeToRunByUnit(double dist, long time) {
		System.out.println("Old TimeToRunByUnit: " + timeForOneUnit + " ms | dist: " + dist + " | time: " + time);
		distanceTot += dist;
		runningTimeTot += time;
		timeForOneUnit = (long) (runningTimeTot / distanceTot);
		System.out.println("New TimeToRunByUnit: " + timeForOneUnit + " ms");
	}

	/** execute l'action donnee par l'ActionGiver */
	private boolean execute(String ac) {
		// X largeur du terrain, Y longueur
		String action[] = ac.split(" ");
		int x0, x1, y0, y1;
		for (TImedMotor m : motors)
			m.checkState();
		switch (action[0]) {
		case "prendrepalet":
			//si le palet n'a pas encore ete touche
			if (!touche) {
				//avance encore un peu pour essayer de l'attraper
				propulsion.runFor(800, true);
				while (propulsion.isRunning()) {
					if (pression.isPressed()) {
						propulsion.stopMoving();
						break;
					}
					propulsion.checkState();
				}
			}
			//calcul le temps ecoule depuis le debut du deplacement
			elapsedTime = System.currentTimeMillis() - start;
			//mets a jour le temps moyen necessaire pour avancer d'une unite
			majTimeToRunByUnit(distance, elapsedTime);
			//ferme les pinces
			graber.close();
			while (graber.isRunning()) {
				graber.checkState();
			}
			//passe dans l'etat "vaPoser"
			vaPoser = true;
			return true;

		case "lacherpalet":
			//est positionne sur la ligne blanche face a l'adversaire
			//avance un peu
			propulsion.runFor(400, true);
			while (propulsion.isRunning()) {
				propulsion.checkState();
			}
			//lache le palet
			graber.open();
			while (graber.isRunning()) {
				graber.checkState();
			}
			//recule apres avoir lacher le palet
			propulsion.runFor(400, false);
			while (propulsion.isRunning()) {
				propulsion.checkState();
			}
			
			//a cette etape on voulait effectue un repositionnement du robot pour repartir exactement du bon endroit
			//repositionnement();
			
			//sort de l'etat "vaPoser"
			vaPoser = false;
			//a fini de ramener le tout premier palet
			firstPass = false;
			return true;

		case "deplacement":
			//remet touche a faux pour le prochain ramassage
			touche = false;
			//recuperation des coordonnees des points de depart et d'arrivee pour le deplacement
			x0 = Integer.valueOf(action[1]);
			y0 = Integer.valueOf(action[2]);
			x1 = Integer.valueOf(action[3]);
			y1 = Integer.valueOf(action[4]);
			myPos = new IntPoint(x0, y0);
			dest = new IntPoint(x1, y1);
			
			//definition d'un point "tout au nord" pour les calculs d'angle"
			IntPoint north = new IntPoint(x0, 16);
			//calcul de la valeur absolue de l'angle forme par arrivee, depart, nord
			int degrees = angle(myPos, dest, north);
			//recupere l'orientation actuelle du robot
			int myOrientation = (int) propulsion.getRotateToNorth();
			//calcul la rotation a effectuer
			int angle = degrees + myOrientation;
			
			//si on va chercher un palet et que ce n'est pas le premier palet
			if (!vaPoser && !firstPass) {
				//determine le sens de la rotation
				if (x1 > x0) {
					aGauche = true;
				} else {
					aGauche = false;
				}
			//si on va poser un palet, regle le sens de rotation sur l'inverse du sens qu'on avait pour prendre le palet
			} else if (vaPoser && !firstPass) {
				aGauche = !aGauche;
			}

			//effectue la rotation
			propulsion.rotate(Math.abs(angle), aGauche, true);
			while (propulsion.isRunning()) {
				propulsion.checkState();
			}
			
			//calcul la distance en ligne droite a parcourir pour atteindre la destination
			distance = distance(myPos, dest);
			//defini un temps de deplacement maximal dans le cas ou un rate le palet
			int ttl = (int) (timeForOneUnit * distance);
			//lance un timer pour savoir le temps ecoule
			start = System.currentTimeMillis();

			// si le robot doit ramener le palet
			if (vaPoser) {
				boolean obsturct = false;
				//si c'est le tout premier palet
				if (firstPass) {
					//verifie si le chemin est obstrue
					ArrayList<IntPoint> points = camera.getPaletsPositions();
					for (IntPoint point : points) {
						if (point.getX() == myPos.getX() && point.getY() > myPos.getY()) {
							obsturct = true;
							break;
						}
					}
				}
				// si le chemin vers le camp adverse est obstrue
				if (obsturct) {
					//effectue un deplacement particulier pour eviter les autres palets
					avoidObstruct();
					return true;
				//avance tout droit
				} else {
					propulsion.run(true);
				}
			// si le robot va chercher un palet, avance tout droit pendant un temps max
			} else {
				propulsion.runFor(ttl, true);
			}
			//couleur de l'axe des X qu'on doit atteindre
			int colorToFind = getCouleurATrouve(dest.getX());
			//variables pour le cas particulier des lignes noir, dans certains cas on peut croiser deux lignes noir et il faut donc ignorer la premiere
			int compteurNoir = 0;
			boolean doitCroiser = myPos.getX() != dest.getX();
			boolean nePasCompterPremierNoir = dest.getY()>6?true:false;
			//booleen pour savoir si on a deja croise la ligne de couleur qu'on doit atteindre
			boolean aCroise = false;
			
			//tant que le robot se deplace
			while (propulsion.isRunning()) {
				propulsion.checkState();
				// conditions d'arret des roues et de sortie de la boucle:
				//si on va chercher un palet et qu'on en touche un OU si on va poser un palet et qu'on croise la ligne blanche
				if ((!vaPoser && pression.isPressed()) || (vaPoser && color.getCurrentColor() == Color.WHITE)) {
					touche = true;
					//s'arrete
					propulsion.stopMoving();
					//si le robot a attrape un palet et qu'il n'a pas encore croise la ligne avance un tout petit peu pour se centrer sur cette ligne
					if(!vaPoser && !aCroise) {
						propulsion.runFor(200, true);
						while (propulsion.isRunning()) {
							propulsion.checkState();
						}
					}
					//mets a jour ma position actuelle
					myPos = dest;
					return true;
				//si le robot croise la ligne de couleur qu'il ne doit pas franchir, se repositionne dans l'axe de cette ligne pour chercher le palet
				} else if(!vaPoser && doitCroiser && !firstPass && (color.getCurrentColor() == colorToFind && !nePasCompterPremierNoir
							|| nePasCompterPremierNoir && compteurNoir==2 && color.getCurrentColor() == Color.BLACK)) {
					propulsion.stopMoving();
					propulsion.runFor(200, true);
					while (propulsion.isRunning()) {
						propulsion.checkState();
					}
					int resteAngle = 180-angle;
					propulsion.rotate(resteAngle, aGauche, true);
					while (propulsion.isRunning()) {
						propulsion.checkState();
					}
					aCroise = true;
					propulsion.runFor(500,true);
				} else if(!vaPoser && doitCroiser && !firstPass && nePasCompterPremierNoir && compteurNoir==0 && color.getCurrentColor() == Color.BLACK) {
					compteurNoir++;
				} else if(!vaPoser && doitCroiser && !firstPass && nePasCompterPremierNoir && compteurNoir==1 && color.getCurrentColor() == Color.GRAY) {
					compteurNoir++;
				}
			}
			return true;
		default:
			return false;
		}
	}
	
	/** retourne la valeur de la couleur correspondant au X donne en parametre */
	private int getCouleurATrouve(int x) {
		int colors[] = {Color.YELLOW, Color.BLACK, Color.RED};
		//si on est parti a droite de la camera, inverse jaune et rouge
		if(!seekLeft) {
			colors[0] = Color.RED;
			colors[2] = Color.YELLOW;
		}
		//determine la couleur qu'on doit croiser
		return colors[(x/3)-1];
	}
	
	
	/**
	 *  repositionne le robot sur le croisement de ligne blanche et la ligne de couleur correspondante a sa position en x 
	 *  /!\ n'est pas utilise car ne fonctionne pas comme souhaite a cause de la courte distance entre le centre des roues et le detecteur de couleurs
	 **/
	private void repositionnement() {
		//se positionne face a la ligne blanche
		propulsion.rotate(R2D2Constants.FULL_CIRCLE, true, false);
		while (propulsion.isRunning()) {
			propulsion.checkState();
			if(color.getCurrentColor() == Color.WHITE) {
				propulsion.stopMoving();
			}
		}
		
		boolean positionOk = false;
		//determine la couleur qu'on doit croiser
		int colorToFind = getCouleurATrouve(myPos.getX()/3);
		//valeurs d'angles acceptees
		int minAngleAccepted = 85;
		int maxAngleAccepted = 95;
		while(!positionOk) {
			//rotation a gauche jusqu'a trouver la ligne de couleur 
			propulsion.rotate(R2D2Constants.FULL_CIRCLE, true, false);
			while (propulsion.isRunning()) {
				propulsion.checkState();
				int currentColor = color.getCurrentColor();
				if(currentColor != Color.GRAY) {			
					if(currentColor == colorToFind) {
						//trouve la bonne couleur
						if(currentColor == Color.YELLOW) System.out.println("JAUNE");
						else if(currentColor == Color.BLACK) System.out.println("NOIR");
						else if(currentColor == Color.RED) System.out.println("ROUGE");
						double rotation = (propulsion.getRotateToNorth() - 90);
						//si l'angle est satisfaisant, on sort
						if(rotation >= minAngleAccepted && rotation <= maxAngleAccepted) {
							positionOk = true;
							propulsion.stopMoving();
						}
						//sinon on ajuste sa position et on recommence
						else {
							System.out.println("angle pas acceptable: "+rotation+"�");
							//on se re-aligne sur la ligne blanche
							propulsion.rotate(R2D2Constants.FULL_CIRCLE, false, false);
							while (propulsion.isRunning()) {
								propulsion.checkState();
								if(color.getCurrentColor() == Color.WHITE) {
									propulsion.stopMoving();
									//face a la ligne blanche
								}
							}
							//rotation trop petite = trop a droite
							if(rotation < minAngleAccepted) {
								//recule face a la ligne blanche
								propulsion.runFor(150, false);
								while (propulsion.isRunning()) {
									propulsion.checkState();
								}
							} 
							//rotation trop grande (>95) = trop a gauche
							else {
								//avance sur la ligne blanche
								propulsion.runFor(150, true);
								while (propulsion.isRunning()) {
									propulsion.checkState();
								}
							}	
						}
					}
				}
				
			}
			if(!positionOk) {
				//aucune couleur trouve, je m'avance un peu
				propulsion.runFor(500, true);
				while (propulsion.isRunning()) {
					propulsion.checkState();
				}
			}
		}
		//repositionnement final
		propulsion.rotate(R2D2Constants.HALF_CIRCLE, false, false);
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

		//execute une roation pour se positionner face a sa destination
		propulsion.rotate(Math.abs(angle)+2, left, true);
		while (propulsion.isRunning()) {
			propulsion.checkState();
		}

		boolean croise = false;
		int compteurNoir = 0;
		//avance tout droit jusqu'a croiser la ligne voulue
		propulsion.run(true);
		while (propulsion.isRunning()) {
			propulsion.checkState();
			int col = color.getCurrentColor();
			int colorToFind = getCouleurATrouve(dest.getX());
			//si on doit croiser une ligne jaune ou rouge
			if(!croise && col == colorToFind && colorToFind != Color.BLACK) {
				croise = true;
				propulsion.stopMoving();
				propulsion.runFor(200, true);
				while (propulsion.isRunning()) {
					propulsion.checkState();
				}
				propulsion.rotate(Math.abs(angle)+2, !left, true);
				while (propulsion.isRunning()) {
					propulsion.checkState();
				}
				propulsion.run(true);
			//si on doit croiser une ligne noir
			} else if(!croise && compteurNoir==0 && col == Color.BLACK) {
				compteurNoir++;
			} else if(!croise && compteurNoir==1 && col == Color.GRAY) {
				compteurNoir++;
			} else if(!croise && compteurNoir==2 && col == Color.BLACK) {
				croise = true;
				propulsion.stopMoving();
				propulsion.runFor(200, true);
				while (propulsion.isRunning()) {
					propulsion.checkState();
				}
				propulsion.rotate(Math.abs(angle)+2, !left, true);
				while (propulsion.isRunning()) {
					propulsion.checkState();
				}
				propulsion.run(true);
			//pour rentrer au camp adverse
			} else if (col == Color.WHITE) {
				propulsion.stopMoving();
			}
		}
		
		if(!croise) {
			//se repositionne face au camp adverse
			propulsion.rotate(Math.abs(angle)+2, !left, true);
			while (propulsion.isRunning()) {
				propulsion.checkState();
			}
		}

		//mets a jour la position actuelle
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

	/** retourne l'angle en degrees en p1 forme par les points p2 et p3 */
	private int angle(IntPoint p1, IntPoint p2, IntPoint p3) {
		double ab = distance(p1, p2);
		double ac = distance(p1, p3);
		double bc = distance(p3, p2);
		double rad = Math.acos(((Math.pow(ab, 2) + Math.pow(ac, 2) - Math.pow(bc, 2)) / (2 * ab * ac)));
		double degree = (rad * 180) / Math.PI;
		return (int) Math.round(degree);
	}

	/** boucle d'execution principale */
	private void mainLoop() {
		boolean run = true;
		boolean pasDeProbleme = true;

		screen.clearPrintln();
		screen.clearDraw();
		screen.drawText("Preparation !");
		ArrayList<String> goals = actionGiver.findGoals(myPos);
		
		screen.clearPrintln();
		screen.clearDraw();
		screen.drawText("GO ? !");
		input.waitAny();

		propulsion.seDegreeToNorth(0);
		//tant qu'il y a une action a executer
		while (run) {
			if (goals == null)
				run = false;
			else {
				//execute les differentes actions donnees par l'action giver
				for (String goal : goals) {
					screen.drawText("Action", goal);
					System.out.println(goal);
					if (goal != null) {
						pasDeProbleme = execute(goal);
					}
				}
				screen.clearPrintln();
				screen.clearDraw();
				//reflechi a son prochain coup
				screen.drawText("Reflexion", "Calcul de l'itineraire", "en cours");
				goals = actionGiver.findGoals(myPos);
				//si on est en mode thread, calcul le coup d'apres
				if (ag) {
					AGExpert age = ((AGExpert)actionGiver).clone();
					actionGiver = age;
					((AGExpert) actionGiver).start();
				}
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
