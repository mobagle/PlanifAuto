import camera.Camera;
import camera.Server;

public class MainCamera {
	
	public static void main(String[] args) {
		// Lancement de la camera 1er boolean pour la calibration, 2nd pour l'affichage des points reçus
		Server s = new Server(null, false, false);
		s.start();
	}
}