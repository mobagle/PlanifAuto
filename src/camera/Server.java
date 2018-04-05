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

	/**
	 * @param sl un objet (EyeOfMarvin dans ce cas) permettant de traiter la reception de la liste de points.
	 */
	public Server(Camera c, boolean calibrer){
		super("Server");
		this.calibreTraducteur	= calibrer;
		this.camera 			= c;
		this.packet 			= new DatagramPacket(this.buffer, this.buffer.length);
		this.lastPointsReceived	= new ArrayList<IntPoint>();
		try {
			this.dsocket = new DatagramSocket(PORT);
		} catch (SocketException e1) {
			System.out.println("[SERVER]                : Erreur, DatagramSocket non initialisé");
			e1.printStackTrace();
		}
		//System.out.println("[SERVER]                : Initialized");
	}
			
	/**
	 * Récupère les positions des items fournies par la caméra, les encode en Item et transmet cette liste a EyeOfMarvin pour traitement
	 */
	@Override
	public void run() {
		//System.out.println("[SERVER]                : Started");
		this.setPriority(Thread.NORM_PRIORITY);
		while(! isInterrupted() && !this.stop){
			try {
				this.dsocket.receive(this.packet);
			} catch (IOException e) {
				this.stop = true;
			}
			//System.out.println("[SERVER]                : Packet receive");
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
				//System.out.println("[SERVER]                : Cam calibre");
			}
			else this.camera.receiveRawPoints(this.lastPointsReceived);
			this.packet.setLength(this.buffer.length);
		}
		//System.out.println("[SERVER]                : Finished");
	}

	@Override
	public void interrupt(){
		this.dsocket.close();
		this.stop = true;
	}
}
