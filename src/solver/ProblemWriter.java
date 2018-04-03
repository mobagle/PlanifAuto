package solver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/*
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
*/

import fr.uga.pddl4j.planners.hsp.HSP;
import utils.IntPoint;

public class ProblemWriter {
	Path path = FileSystems.getDefault().getPath(".");
	
	public String filename = new File("default.pddl").getAbsolutePath();

	/* Ecriture du probleme à résoudre au format pddl
	 */
	public void write(IntPoint departRobot, ArrayList<IntPoint> palets) {
		try {
			
			FileWriter fichier = new FileWriter(filename);
			// Contenu fixe du domaine
			String contenu = domaine;
			
			// Position de départ robot
			contenu += "        (is-on x"+departRobot.getX()+" y"+departRobot.getY()+")\n";
			
			// Positions des palets
			for (IntPoint p : palets) {
				contenu += "        (palet-is-on x"+p.getX()+" y"+p.getY()+")\n";	
			}
			
			// But
			contenu +=  "        )\n" + 
						"    (:goal (and\n" +
						"    	(aPosePalet)\n" +
						"		(is-ony y12))))";
			fichier.write (contenu);
			fichier.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    // Domaine utilisé, lachage des palets dans le camps adverse
    final private String domaine = 
    		"(define (problem palets)\n" + 
			"    (:domain hanoi)\n" + 
			"    (:objects\n" + 
			"        x0 x1 x2 x3 x4 x5 x6 x7 x8 x9 x10 x11 x12 - posX\n" + 
			"        y0 y1 y2 y3 y4 y5 y6 y7 y8 y9 y10 y11 y12 - posY)\n" + 
			"    (:init         \n" + 
			"        (depX x0 x1) (depY y0 y1)\n" + 
			"        (depX x1 x2) (depY y1 y2)\n" + 
			"        (depX x2 x3) (depY y2 y3)\n" + 
			"        (depX x3 x4) (depY y3 y4)\n" + 
			"        (depX x4 x5) (depY y4 y5)\n" + 
			"        (depX x5 x6) (depY y5 y6)\n" + 
			"        (depX x6 x7) (depY y6 y7)\n" + 
			"        (depX x7 x8) (depY y7 y8)\n" + 
			"        (depX x8 x9) (depY y8 y9)\n" + 
			"        (depX x9 x10) (depY y9 y10)\n" + 
			"        (depX x10 x11) (depY y10 y11)\n" +
			"		 (depX x11 x12) (depY y11 y12)\n" +
			"        (peut-lacher x0 y12)\n" + 
			"        (peut-lacher x1 y12)\n" + 
			"        (peut-lacher x2 y12)\n" + 
			"        (peut-lacher x3 y12)\n" + 
			"        (peut-lacher x4 y12)\n" + 
			"        (peut-lacher x5 y12)\n" + 
			"        (peut-lacher x6 y12)\n" + 
			"        (peut-lacher x7 y12)\n" + 
			"        (peut-lacher x8 y12)\n" + 
			"        (peut-lacher x9 y12)\n" + 
			"        (peut-lacher x10 y12)\n" + 
			"        (peut-lacher x11 y12)\n" +
			"        (peut-lacher x12 y12)\n" +
			"        \n";
}
