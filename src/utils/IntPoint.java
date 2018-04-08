package utils;

/**
 * Classe representant un point ayant des coordonn�es enti�re dans un rep�re cart�sien.
 * Permet des op�rations simple sur les points.
 * @author paul.carretero, florent.chastagner
 * @see Item
 */
public class IntPoint {
	
	/**
	 * coordonn�e du point sur l'axe x
	 */
	
	protected int x;
	/**
	 * coordonn�e du point sur l'axe y
	 */
	protected int y;
	
	/**
	 * @param x coordonn�e du point sur l'axe x
	 * @param y coordonn�e du point sur l'axe y
	 */
	public IntPoint(int x, int y){
		this.x = x;
		this.y = y;
	}

	/**
	 * @return retourne la coordonn� x
	 */
	public int getX(){
		return this.x;
	}
	
	/**
	 * @return retourne la coordonn� y
	 */
	public int getY(){
		return this.y;
	}
	
	/**
	 * @param x une nouvelle coordonn� x
	 * @return met � jour la coordonn� x
	 */
	public int setX(final int x){
		return this.x = x;
	}
	
	/**
	 * @param y une nouvelle coordonn� y
	 * @return met � jour la coordonn� y
	 */
	public int setY(final int y){
		return this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntPoint other = (IntPoint) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	
	/**
	 * Met � jour les coordonn�s du point
	 * @param x coordonn� du point sur l'axe x
	 * @param y coordonn� du point sur l'axe y
	 */
	public void update(final int x, final int y){
		this.x = x;
		this.y = y;
	}

	/**
	 * @param p2 un IntPoint non null
	 * @return la distance entre ce point et p2 (entier positif)
	 */
	public int getDistance(final IntPoint p2){
		return (int) Math.round(Math.sqrt((this.x-p2.x)*(this.x-p2.x) + (this.y-p2.y)*(this.y-p2.y)));
	}
	
	/**
	 * @return une nouvelle instance d'un objet point de la biblioth�que LeJos
	 * @see Point
	 *
	public Point toLejosPoint(){
		return new Point(this.x,this.y);
	}*/
	
	/**
	 * @param p2 une autre IntPoint
	 * @return le vecteur en partance de ce point vers P2
	 *
	public IntPoint computeVector(final IntPoint p2){
		return new IntPoint((p2.x() - this.x()) , (p2.y() - this.y()));
	}*/
	
	/**
	 * calcul le point d'intercection avec la droite � 20 cm de la ligne d'objectif
	 * @param vecteur un IntPoint repr�sentant un vecteur
	 * @return retourne le point d'interction entre la droite partant de ce point avec le vecteur et la ligne de d�fense
	 *
	public IntPoint getIntersection(final IntPoint vecteur){
		try{
			int coeff = Math.max(Main.Y_DEFEND_WHITE - this.y(),this.y() - Main.Y_DEFEND_WHITE) / vecteur.y();
			
			IntPoint res = new IntPoint(vecteur.x() * coeff + this.x(), vecteur.y() * coeff + this.y());
			
			if(res.y() < 1500){
				res.setY(Main.Y_DEFEND_WHITE + 200);
			}
			else{
				res.setY(Main.Y_DEFEND_WHITE - 200);
			}
			
			return res;
		}
		catch (Exception e) {
			// division par 0
			return null;
		}
	}*/
	
	@Override
	public String toString(){
		return "[X = " + this.x + " Y = " + this.y + "]";
	}
}
