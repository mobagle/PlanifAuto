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

	/* Ecriture du probleme à résoudre
	 * 	Au format pddl
	 */
	public void write(IntPoint departRobot, IntPoint arriveRobot, ArrayList<IntPoint> palets) {
		try {
			
			FileWriter fichier = new FileWriter(filename);
			// Contenu fixe du domaine
			String contenu = domaine2;
			
			// Position de départ robot
			contenu = contenu +	"        (is-on x"+departRobot.getX()+" y"+departRobot.getY()+")\n";
			
			// Positions des palets
			for (IntPoint p : palets) {
				contenu = contenu+"        (palet-is-on x"+p.getX()+" y"+p.getY()+")\n";	
			}
			
			// But
			contenu = contenu + "        )\n" + 
								"    (:goal (aPosePalet)\n" +
								"    ))";

			fichier.write (contenu);
			fichier.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    // Domaine avec les raccourcies, non utilisé car probleme de décision (tous les deplacements ne prennent pas le même temps)
    final private String domaine = 
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
			"        \n";
    
    // Probleme initiale, probleme pour lacher les palets au bon endroit
    final private String domaine2 = 
    		"(define (problem hanoi-tower)\n" + 
			"    (:domain hanoi)\n" + 
			"    (:objects\n" + 
			"        x0 x1 x2 x3 x4 x5 x6 x7 x8 x9 x10 x11 - posX\n" + 
			"        y0 y1 y2 y3 y4 y5 y6 y7 y8 y9 y10 y11 - posY)\n" + 
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
			"        (depX x10 x11) (depY y10 y11)" +
			"\n" + 
			"        \n";
    
    // Domaine utilisé, lachage des palets dans le camps adverse
    final private String domaine3 = 
    		"(define (problem hanoi-tower)\n" + 
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
