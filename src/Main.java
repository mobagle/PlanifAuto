import camera.Camera;
import camera.Server;
import controller.Controler;

import lejos.utility.Delay;

public class Main {

	public static void main(String[] args) {
		Camera cam = new Camera();

		// Lancement de la camera 1er boolean pour la calibration, 2nd true si le server est pour un robot
		Server s = new Server(cam, false, true);
		s.start();

		//Lancement de la Brique
		
		Controler controler = new Controler(cam);
		try{
			controler.start();
		}catch(Throwable e){
			e.printStackTrace();
			Delay.msDelay(10000);
		}
		//System.exit(0);
	}

}
