package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import fr.uga.pddl4j.planners.hsp.HSP;

public class ProblemWriter {

	String filename;

	public ProblemWriter() {
		filename = "";
	}
	
	ProblemWriter(String fichier) {
		filename = fichier;
	}
	
	public void setFilename(String fichier) {
		filename = fichier;
	}
	
	/* Fonction qui trouve la liste des actions à effectuer */
	public ArrayList<String> findActions(IntPoint departRobot, IntPoint arriveRobot, ArrayList<IntPoint> palets) {
		this.write(departRobot, arriveRobot, palets);
		String plan;
		try {
			plan = this.testHSPplan();
			System.out.println(plan);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Decouper les actions
		String[] actiontab = null; //plan.split(arg0);
				
		//Les ajouter dans l'arraylist
		ArrayList<String> actions = new ArrayList<String>();
		for (int i = 0; i<actiontab.length; i++) actions.add(actiontab[i]);
		
		return null;
	}
	
	/* Ecriture du programme à résoudre */
	public void write(IntPoint departRobot, IntPoint arriveRobot, ArrayList<IntPoint> palets) {
		try {
			FileWriter fichier = new FileWriter(filename.equals("") ? "default.pddl" : filename);
		
			String contenu = 
				"(define (problem hanoi-tower)\n" + 
				"    (:domain hanoi)\n" + 
				"    (:objects\n" + 
				"        x0 x1 x2 x3 x4 x5 x6 x7 x8 x9 x10 x11 - posX\n" + 
				"        y0 y1 y2 y3 y4 y5 y6 y7 y8 y9 y10 y11 - posY)\n" + 
				"    (:init         \n" + 
				"        (depX x0 x1) (depY y0 y1)\n" + 
				"        (depX x0 x2) (depY y0 y2)\n" + 
				"        (depX x0 x3) (depY y0 y3)\n" + 
				"        (depX x0 x4) (depY y0 y4)\n" + 
				"        (depX x0 x5) (depY y0 y5)\n" + 
				"        (depX x0 x6) (depY y0 y6)\n" + 
				"        (depX x0 x7) (depY y0 y7)\n" + 
				"        (depX x0 x8) (depY y0 y8)\n" + 
				"        (depX x0 x9) (depY y0 y9)\n" + 
				"        (depX x0 x10) (depY y0 y10)\n" + 
				"        (depX x0 x11) (depY y0 y11)\n" + 
				"        (depX x1 x2) (depY y1 y2)\n" + 
				"        (depX x1 x3) (depY y1 y3)\n" + 
				"        (depX x1 x4) (depY y1 y4)\n" + 
				"        (depX x1 x5) (depY y1 y5)\n" + 
				"        (depX x1 x6) (depY y1 y6)\n" + 
				"        (depX x1 x7) (depY y1 y7)\n" + 
				"        (depX x1 x8) (depY y1 y8)\n" + 
				"        (depX x1 x9) (depY y1 y9)\n" + 
				"        (depX x1 x10) (depY y1 y10)\n" + 
				"        (depX x1 x11) (depY y1 y11)\n" + 
				"        (depX x2 x3) (depY y2 y3)\n" + 
				"        (depX x2 x4) (depY y2 y4)\n" + 
				"        (depX x2 x5) (depY y2 y5)\n" + 
				"        (depX x2 x6) (depY y2 y6)\n" + 
				"        (depX x2 x7) (depY y2 y7)\n" + 
				"        (depX x2 x8) (depY y2 y8)\n" + 
				"        (depX x2 x9) (depY y2 y9)\n" + 
				"        (depX x2 x10) (depY y2 y10)\n" + 
				"        (depX x2 x11) (depY y2 y11)\n" + 
				"        (depX x3 x4) (depY y3 y4)\n" + 
				"        (depX x3 x5) (depY y3 y5)\n" + 
				"        (depX x3 x6) (depY y3 y6)\n" + 
				"        (depX x3 x7) (depY y3 y7)\n" + 
				"        (depX x3 x8) (depY y3 y8)\n" + 
				"        (depX x3 x9) (depY y3 y9)\n" + 
				"        (depX x3 x10) (depY y3 y10)\n" + 
				"        (depX x3 x11) (depY y3 y11)\n" + 
				"        (depX x4 x5) (depY y4 y5)\n" + 
				"        (depX x4 x6) (depY y4 y6)\n" + 
				"        (depX x4 x7) (depY y4 y7)\n" + 
				"        (depX x4 x8) (depY y4 y8)\n" + 
				"        (depX x4 x9) (depY y4 y9)\n" + 
				"        (depX x4 x10) (depY y4 y10)\n" + 
				"        (depX x4 x11) (depY y4 y11)\n" + 
				"        (depX x5 x6) (depY y5 y6)\n" + 
				"        (depX x5 x7) (depY y5 y7)\n" + 
				"        (depX x5 x8) (depY y5 y8)\n" + 
				"        (depX x5 x9) (depY y5 y9)\n" + 
				"        (depX x5 x10) (depY y5 y10)\n" + 
				"        (depX x5 x11) (depY y5 y11)\n" + 
				"        (depX x6 x7) (depY y6 y7)\n" + 
				"        (depX x6 x8) (depY y6 y8)\n" + 
				"        (depX x6 x9) (depY y6 y9)\n" + 
				"        (depX x6 x10) (depY y6 y10)\n" + 
				"        (depX x6 x11) (depY y6 y11)\n" + 
				"        (depX x7 x8) (depY y7 y8)\n" + 
				"        (depX x7 x9) (depY y7 y9)\n" + 
				"        (depX x7 x10) (depY y7 y10)\n" + 
				"        (depX x7 x11) (depY y7 y11)\n" + 
				"        (depX x8 x9) (depY y8 y9)\n" + 
				"        (depX x8 x10) (depY y8 y10)\n" + 
				"        (depX x8 x11) (depY y8 y11)\n" + 
				"        (depX x9 x10) (depY y9 y10)\n" + 
				"        (depX x9 x11) (depY y9 y11)\n" + 
				"        (depX x10 x11) (depY y10 y11)" +
				"\n" + 
				"        \n" + 
				"        (is-on x"+departRobot.getX()+" y"+departRobot.getY()+")\n";
				
			for (IntPoint p : palets) {
				contenu = contenu+"        (palet-is-on x"+p.getX()+" y"+p.getY()+")\n";	
			}
			contenu = contenu + "        )\n" + 
								"    (:goal\n" +
								"        (and(palet-is-on x0 y0)\n";		
			fichier.write (contenu);
			fichier.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* Résoud le probleme avec pddl4j */
    public String testHSPplan() throws Exception {
        String[] args = new String[6];
        args[0] = "-o";
        args[1] = "pddlfiles/domain.pddl";
        //args[1] = "pddl/gripper/domain.pddl";
        args[2] = "-f";
        args[3] = "pddlfiles/"+filename.equals("") != null ? "default.pddl" : filename ;
        //args[3] = "pddl/gripper/p01.pddl";
        args[4] = "-t";
        args[5] = "10";

        String jsonPlan = HSP.resolveAsJsonPlan(args);
        /*
        Assert.assertFalse(jsonPlan == null);
        Assert.assertFalse(jsonPlan.contentEquals(""));
        Assert.assertTrue(jsonPlan.contentEquals(validGripperP01JSON));
        */
        return jsonPlan;
    }
    /*
    public static void main (String[] args){
    	IntPoint robot = new IntPoint(1, 1);
    	ArrayList<IntPoint> pos = new ArrayList<IntPoint>();
    	pos.add(new IntPoint(5, 8));
    	findActions(robot, null, pos);
    
    }*/
    
    
    
}
