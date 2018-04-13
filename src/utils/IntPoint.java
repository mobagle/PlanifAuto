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
	
	@Override
	public String toString(){
		return "[X = " + this.x + " Y = " + this.y + "]";
	}
}
