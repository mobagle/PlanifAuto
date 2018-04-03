package solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Properties;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.planners.hsp.HSP;
import utils.IntPoint;

public class Solver {
	
	final String positionFiles = "./";
	
	/* Fonction principale 
	 * - trouve la liste des actions à effectuer 
	 * - la parse
	 */
	public ArrayList<String> findActions(IntPoint departRobot, ArrayList<IntPoint> palets) {
		// Ecriture du probleme
		ProblemWriter pw = new ProblemWriter();
		pw.write(departRobot, palets);
		String plan = "";
		
		// Récuperation de la sortie du solver
		try {
			plan = this.testHSPplan();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Recuperation des actions
		Parser pars = new Parser();
		ArrayList<String> actions = pars.getActions(plan);

		return actions;
	}

	/* Lance le probleme avec pddl4j */
    public String testHSPplan() throws Exception {
    	
        String[] args = new String[4];
        args[0] = "-o";
        args[1] =  "./domain.pddl";
        args[2] = "-f";
        args[3] = "./default.pddl";
        //args[4] = "-t";
        //args[5] = "10";
        
        return HSP.resolve(args);
    }
}
