package utils;

import java.util.ArrayList;

import lejos.robotics.Color;

public class ColorGrid {
	//colonnes
	public final static VerticalLine BLANC_GAUCHE = new VerticalLine(0);
	public final static VerticalLine VERT = new VerticalLine(1);
	public final static VerticalLine NOIR_VERTICAL = new VerticalLine(2);
	public final static VerticalLine BLEU = new VerticalLine(3);
	public final static VerticalLine BLANC_DROITE = new VerticalLine(4);
	//lignes
	public final static HorizontalLine ROUGE = new HorizontalLine(0);
	public final static HorizontalLine NOIR_HORIZONTAL = new HorizontalLine(1);
	public final static HorizontalLine JAUNE = new HorizontalLine(2);
	
	private boolean grille[][];
	
	public ColorGrid() {
		for(int i = 0; i<5; i++) {
			for(int j = 0; j<3; j++) {
				if(i == 1 || i==2 || i==3) {
					grille[i][j] = true;
				}
				grille[i][j] = false;
			}
		}
	}
	
	public boolean hasPalet(VerticalLine v, HorizontalLine h) {
		return grille[v.val][h.val];
	}
	
	public void setPalet(VerticalLine v, HorizontalLine h, boolean value) {
		grille[v.val][h.val] = value;
	}
	
	
		
}
