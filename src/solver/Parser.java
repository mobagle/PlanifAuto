package solver;

import java.util.ArrayList;

public class Parser {

	/* Fonction principale, transforme le String (Json) en ArrayList d'actions à effectuer */
	public ArrayList<String> getActions(String plan) {
		// Tableau avec toutes les actions
		String[] tab = getTabActions(plan);
		
		// Suppression des actions identiques à la suite
		//simplifier(tab);
		
		// Supprime les déplacements en angles droits
		//raccourcie(tab);
		
		// Remplissage de l'ArrayList des actions
		ArrayList<String> resultat = new ArrayList<String>();
		for(int i = 0; i<tab.length; i++) {
			if (tab[i] != null) {
				resultat.add(tab[i]);
			}
		}
		return resultat;
	}
	
	/* Parser 
	 * Cree un tableau d'action en fonction du string plan retourné par pddl 
	 */
	public String[] getTabActions(String plan) {
		return null;
	}
	
	/* Remet dans l'ordre des actions à effectuer par le robot */
	public String[] actionDansLOrdre(ArrayList<String> listString, int size) {
		String[] result = new String[size]; // Modification de sa taille en fonction du nombre d'actions
		
		for(String s : listString) {
			if (getPremierMot(s).equals("Action")) { // Une action est reconnue
				String[] action = s.split(" ");
				int num = Integer.parseInt(action[1]);
				String act = action[3] + " " + action[5].substring(1) + " " + action[6].substring(1);
				if (!(action[3].equals("lacherpalet") || action[3].equals("prendrepalet"))) { // Une fonction avec 3 arguments
					act = act + " " + action[7].substring(1);
				}
				result[num] = act; // Les actions se remettent dans l'ordre
			}
		}
		return result;
	}
	
	/* Modifie les actions
	 * Si action1 est égale à action2, 
	 * 	nous partons de la position initiale de l'action1
	 * 	et nous arrivons à la position d'arrivé de action2
	 */
	private void simplifier(String[] tab) {
		int size = tab.length;
		for(int i = 0; i<size-1; i++) {
			String action1 = getPremierMot(tab[i]);
			String action2 = getPremierMot(tab[i+1]);
			if (action1 != null && action1.equals(action2)) {
				String[] actionTab1 = tab[i].split(" ");
				String[] actionTab2 = tab[i+1].split(" ");
				String newAction = actionTab1[0]+" "+actionTab1[1]+" "+actionTab2[2]+" "+actionTab1[3];
				tab[i] = newAction;
				suppr(i+1, tab);
				i--;
			}
		}
	}

	/* Modifie les actions
	 * Si il y a un deplacement sur Y suivi d'un deplacement sur Y (resp. Y puis sur X)
	 * C'est des actions sont supprimés et remplacé par une action de deplacement d'un point A à un point B
	 * Tel que : A.X != B.X && A.Y != B.Y
	 */
	private void raccourcie(String[] tab) {
		int size = tab.length;
		for(int i = 0; i<size-1; i++) {
			String action1 = getPremierMot(tab[i]);
			String action2 = getPremierMot(tab[i+1]);
			int res = actionsCorrespondantes(action1, action2);
			if (res > 0) {
				String[] actionTab1 = tab[i].split(" ");
				String[] actionTab2 = tab[i+1].split(" ");
				String newAction = "dep ";
				if (res == 1) {
					newAction += actionTab1[1]+" "+actionTab2[1]+" "+actionTab1[2]+" "+actionTab2[2];
				} else if(res == 2) {
					newAction += actionTab2[1]+" "+actionTab1[1]+" "+actionTab2[2]+" "+actionTab1[2];
				} else {
					System.out.println("ERREUR valeur de 'res' dans raccourcie");
				}
				//System.out.println("newAction ="+newAction);
				tab[i] = newAction;
				suppr(i+1, tab);
				i--;
			}
		}
	}
	
	/* Regarde si action1 est un deplacement sur X (resp. Y), et action2 un deplacmeent sur Y (resp. X)
	 * return :
	 * 	-1	: si une est null
	 *  0	: si les action ne correspondent pas
	 *  1	: si action1 est un deplacement sur X, et action2 un deplacement sur Y
	 *  2	: si action1 est un deplacement sur Y, et action2 un deplacement sur X
	*/
	private int actionsCorrespondantes(String action1, String action2) {
		if (action1 == null || action2 == null) return -1;
		if ((action1.equals("deplacementx1") || action1.equals("deplacementx2")) && (action2.equals("deplacementy1") || action2.equals("deplacementy2"))) return 1;
		if ((action1.equals("deplacementy1") || action1.equals("deplacementy2")) && (action2.equals("deplacementx1") || action2.equals("deplacementx2"))) return 2;	
		return 0;
	}
	
	/* Supprime la case num du tableau tab en deplacement toutes les valeurs du tableau suivant */
	private void suppr(int num, String[] tab) {
		int size = tab.length;
		for(int i = num; i<size-1; i++) tab[i] = tab[i+1];
		tab[size-1] = null;
	}

	/* Recupere le nombre d'action à effectuer */
	public int findNbActions(String a) {
		String[] str = a.split(" ");
		int size = str.length;
		return Integer.parseInt(str[size-1]) +1;
	}
	
	/* Recupere le premier mot d'un string, separe par des espaces */
	public String getPremierMot(String word) {
		if (word == null) return null;
		int i = 0;
		String s = "";
		char c = word.charAt(i);
		while(c != ' ') {
			s = s+c;
			i++;
			c = word.charAt(i);
		}
		return s;
	}
}