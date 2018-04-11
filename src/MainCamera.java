import camera.Camera;
import camera.Server;

public class MainCamera {
	
	public static void main(String[] args) {
		// Lancement de la camera 1er boolean pour la calibration, 2nd si tu es un robot
		Server s = new Server(null, true, false);
		s.start();
	}
}