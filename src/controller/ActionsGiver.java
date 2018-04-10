package controller;

import java.util.ArrayList;

import utils.IntPoint;

public interface ActionsGiver{
	
	public ArrayList<String> findGoals(IntPoint myPos);
	
	public void setSeekLeft(boolean bool);
}
