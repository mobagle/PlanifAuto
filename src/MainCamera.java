import camera.Camera;
import camera.Server;

public class MainCamera {
	
	public static void main(String[] args) {
		Camera cam = new Camera();
		
		// Lancement de la camera 1er boolean pour la calibration, 2nd pour l'affichage des points reçus
		Server s = new Server(cam, false, true);
		s.start();
	}
}