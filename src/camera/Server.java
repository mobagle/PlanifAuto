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
	
	
	/**
	 * @param sl un objet (EyeOfMarvin dans ce cas) permettant de traiter la reception de la liste de points.
	 */
	public Server(Camera c){
		super("Server");
		this.camera 			= c;
		this.packet 			= new DatagramPacket(this.buffer, this.buffer.length);
		this.lastPointsReceived	= new ArrayList<IntPoint>();
		try {
			this.dsocket = new DatagramSocket(PORT);
		} catch (SocketException e1) {
			System.out.println("[SERVER]                : Erreur, DatagramSocket non initialisé");
			e1.printStackTrace();
		}
		System.out.println("[SERVER]                : Initialized");
	}
			
	/**
	 * Récupère les positions des items fournies par la caméra, les encode en Item et transmet cette liste a EyeOfMarvin pour traitement
	 */
	@Override
	public void run() {
		System.out.println("[SERVER]                : Started");
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
					System.out.println(coord[0]+coord[1]+coord[2]);
		        	int x = Integer.parseInt(coord[1]);
		        	int y = Integer.parseInt(coord[2]);
		        	IntPoint p = this.traductionPoint(new IntPoint(x, y));
		        	this.lastPointsReceived.add(p);
				}
	        }
			this.camera.receiveRawPoints(this.lastPointsReceived);
			this.packet.setLength(this.buffer.length);
		}
		System.out.println("[SERVER]                : Finished");
	}
	
	private IntPoint traductionPoint(IntPoint intPoint) {
		if (Controler.seekLeft) {
			return traductionPointLeft(intPoint);
		} else {
			return traductionPointRight(intPoint);
		}
	}

	private IntPoint traductionPointRight(IntPoint intPoint) {
		int x = intPoint.getX();
		int y = intPoint.getY();
		int newX = (int)(Math.abs(200-x)/50)*3;
		int newY = (int)(x/50)*3;
		return new IntPoint(newX, newY);
	}

	private IntPoint traductionPointLeft(IntPoint intPoint) {
		int x = intPoint.getX();
		int y = intPoint.getY();
		int newX = (int)(x/50)*3;
		int newY = (int)(x/50)*3;
		return new IntPoint(newX, newY);
	}

	@Override
	public void interrupt(){
		this.dsocket.close();
		this.stop = true;
	}
}
