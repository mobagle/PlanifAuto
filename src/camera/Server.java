package camera;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

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
	 * temps en ms de la dernière reception des positions
	 */
	private int lastReceivedTimer	= 0;
	
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
		this.camera 				= c;
		this.packet 			= new DatagramPacket(this.buffer, this.buffer.length);
		this.lastPointsReceived	= new ArrayList<IntPoint>();
		
		try {
			this.dsocket = new DatagramSocket(PORT);
		} catch (SocketException e1) {
			//Main.printf("[SERVER]                : Erreur, DatagramSocket non initialisé");
			e1.printStackTrace();
		}
		
		//Main.printf("[SERVER]                : Initialized");
	}
			
	/**
	 * Récupère les positions des items fournies par la caméra, les encode en Item et transmet cette liste a EyeOfMarvin pour traitement
	 */
	@Override
	public void run() {
		//Main.printf("[SERVER]                : Started");
		this.setPriority(Thread.NORM_PRIORITY);
		while(! isInterrupted() && !this.stop){
			try {
				this.dsocket.receive(this.packet);
				//this.lastReceivedTimer = Main.TIMER.getElapsedMs();
			} catch (IOException e) {
				//Main.printf("[SERVER]                : Socket Closed");
				this.stop = true;
			}
			String msg = new String(this.buffer, 0, this.packet.getLength());
			String[] items = msg.split("\n");
			this.lastPointsReceived.clear();
			for (int i = 0; i < items.length; i++) 
	        {
				String[] coord = items[i].split(";");
				if(coord.length == 3){
		        	int x = Integer.parseInt(coord[1]);
		        	int y = 300 - Integer.parseInt(coord[2]); // convertion en mode 'genius'
		        	this.lastPointsReceived.add(new IntPoint(x, y));		        	
				}
	        }
			this.camera.receiveRawPoints(this.lastReceivedTimer,this.lastPointsReceived);
			this.packet.setLength(this.buffer.length);
		}
		//Main.printf("[SERVER]                : Finished");
	}
	
	@Override
	public void interrupt(){
		this.dsocket.close();
		this.stop = true;
	}
}
