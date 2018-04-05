import camera.Camera;
import camera.Server;
import controller.Controler;

import lejos.utility.Delay;

public class Main {

	public static void main(String[] args) {
		Camera cam = new Camera();
		//Camera cam = null;

		// Lancement de la camera
		Server s = new Server(cam, false);
		s.start();
		
		//System.out.println("END");
		
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
