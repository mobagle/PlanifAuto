package solver;

import java.io.File;
import java.util.ArrayList;

import fr.uga.pddl4j.planners.hsp.HSP;

public class Solver {
	
	final String positionFiles = "pddlfiles/";
	
	/* Fonction principale 
	 * - trouve la liste des actions à effectuer 
	 * - la parse
	 */
	public ArrayList<String> findActions(IntPoint departRobot, IntPoint arriveRobot, ArrayList<IntPoint> palets) {
		// Ecriture du probleme
		ProblemWriter pw = new ProblemWriter();
		pw.write(departRobot, arriveRobot, palets);
		String plan = "";
		
		// Récuperation de la sortie du solver
		try {
			plan = this.testHSPplan();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Recuperation des actions
		Jsonparser jspars = new Jsonparser();		
		ArrayList<String> actions = jspars.getActions(plan);
		
		return actions;
	}

	/* Lance le probleme avec pddl4j */
    public String testHSPplan() throws Exception {
        String[] args = new String[6];
        new File("domain.pddl");
        args[0] = "-o";
        args[1] = positionFiles+"domain.pddl";
        args[2] = "-f";
        args[3] = positionFiles+"default.pddl";
        args[4] = "-t";
        args[5] = "10";
        return HSP.resolve(args);
    }
    
}
