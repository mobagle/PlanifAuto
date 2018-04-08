package controller;

import java.util.ArrayList;

import utils.IntPoint;

public interface ActionsGiver{
	
	public ArrayList<String> findGoals(IntPoint myPos);
	
	public ArrayList<String> findGoals();
}
