package camera;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import controller.Controler;
import utils.IntPoint;

public class Server extends Thread{
	
	/**
	 * port sur lequel recevoir les positions des item du terrain fournies par la caméra
	 */
	private static final int PORT	= 8888;
	
	/**
	 * buffer pour la reception des données
	 */
	private byte[] buffer			= new byte[2048];

	/**
	 * vrai si le Thread doit se terminer, faux sinon
	 */
	private volatile boolean stop	= false;
	
	/**
	 * Socket du serveur
	 */
	private DatagramSocket dsocket;
	
	/**
	 * paquet UDP reçu contenant les positions des items
	 */
	private DatagramPacket packet;
	
	/**
	 * Liste d'item contenant les points (bruts) reçu de la caméra.
	 */
	private ArrayList<IntPoint> lastPointsReceived;
	
	
	/**
	 * EyeOfMarvin traitant la liste de position générée
	 */
	private Camera camera;
	
	private boolean calibreTraducteur;
	
	private boolean isarobot;
	
	/**
	 * @param sl un objet (EyeOfMarvin dans ce cas) permettant de traiter la reception de la liste de points.
	 */
	public Server(Camera c, boolean calibrer, boolean robot){
		super("Server");
		this.calibreTraducteur	= calibrer;
		this.isarobot			= robot;
		this.camera 			= c;
		this.packet 			= new DatagramPacket(this.buffer, this.buffer.length);
		this.lastPointsReceived	= new ArrayList<IntPoint>();
		try {
			this.dsocket = new DatagramSocket(PORT);
		} catch (SocketException e1) {
			System.out.println("[SERVER]                : Erreur, DatagramSocket non initialisé");
			e1.printStackTrace();
		}
	}
			
	/**
	 * Récupère les positions des items fournies par la caméra, les encode en Item et transmet cette liste a EyeOfMarvin pour traitement
	 */
	@Override
	public void run() {
		if (isarobot) serverRobot();
		else serverMecano();		
	}

	public void serverMecano(){
		this.setPriority(Thread.NORM_PRIORITY);
		while(! isInterrupted() && !this.stop){
			try {
				this.dsocket.receive(this.packet);
			} catch (IOException e) {
				this.stop = true;
			}
			String msg = new String(this.buffer, 0, this.packet.getLength());
			String[] items = msg.split("\n");
			this.lastPointsReceived.clear();
			for (int i = 0; i < items.length; i++) {
				String[] coord = items[i].split(";");
				if(coord.length == 3){
		        	int x = Integer.parseInt(coord[1]);
		        	int y = Integer.parseInt(coord[2]);
		        	this.lastPointsReceived.add(new IntPoint(x, y));
				}
	        }
			afficher(this.lastPointsReceived);
			this.packet.setLength(this.buffer.length);
		}
	}
	
	private void afficher(ArrayList<IntPoint> packet) {
		Traducteur t = new Traducteur();
		t.setSeekLeft(true);	// a gauche de la camera
		//t.setSeekLeft(false);	// a droite de la camera
		System.out.println("Reception des points :");
		for (IntPoint ip : t.traduire(packet)) System.out.println(" - "+ip);
	}
		
		
	public void serverRobot(){
		this.setPriority(Thread.NORM_PRIORITY);
		while(! isInterrupted() && !this.stop){
			try {
				this.dsocket.receive(this.packet);
			} catch (IOException e) {
				this.stop = true;
			}
			String msg = new String(this.buffer, 0, this.packet.getLength());
			String[] items = msg.split("\n");
			this.lastPointsReceived.clear();
			for (int i = 0; i < items.length; i++) {
				String[] coord = items[i].split(";");
				if(coord.length == 3){
		        	int x = Integer.parseInt(coord[1]);
		        	int y = Integer.parseInt(coord[2]);
		        	this.lastPointsReceived.add(new IntPoint(x, y));
				}
	        }
			if (calibreTraducteur) {
				this.camera.calibrer(this.lastPointsReceived);
				calibreTraducteur = false;
			}
			else this.camera.receiveRawPoints(this.lastPointsReceived);
			this.packet.setLength(this.buffer.length);
		}
	}

	@Override
	public void interrupt(){
		this.dsocket.close();
		this.stop = true;
	}
}
