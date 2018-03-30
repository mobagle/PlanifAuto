package solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Properties;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.exceptions.UsageException;
import fr.uga.pddl4j.planners.hsp.HSP;
import utils.IntPoint;

public class Solver {
	
	final String positionFiles = "./";
	
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
		Parser pars = new Parser();
		ArrayList<String> actions = pars.getActions(plan);

		return actions;
	}

	/* Lance le probleme avec pddl4j */
    public String testHSPplan() throws Exception {
    	
        String[] args = new String[4];
        args[0] = "-o";
        args[1] =  "./domain.pddl";
        //args[1] =  "./src/solver/pddlfiles/domain.pddl";
        args[2] = "-f";
        args[3] = "./default.pddl";
        //args[3] = "./src/solver/pddlfiles/default.pddl";
        //args[4] = "-t";
        //args[5] = "10";
        //return HSP.resolve(args);
        
        
        String res = "";
        try {
            // Parse the command line
            final Properties arguments = HSP.parseArguments(args);
            // Create the planner
            HSP planner = new HSP(arguments);
            // Parse and encode the PDDL file into compact representation
            final CodedProblem problem = planner.parseAndEncode();

            if (problem != null) {
                // Search for a solution and print the result
                res = planner.search2(problem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    
}
