package pitch.simulation;

import java.util.ArrayList;
import java.util.List;

public class Trick {
	//public int takerID;
	public List<Lay> trickLaysList;
	public Lay highestLay;
	public int catchablePoints;
	public int deuceLayer;

	public Trick() {
		trickLaysList = new ArrayList<Lay>(6);
		catchablePoints = 0;
		deuceLayer = Player.NONE;
		highestLay = new Lay(Player.NONE, new Card(), "NONE");
	}

	public String toString() {
		String res = "==== Trick ===\n";
		for (int i = 0; i < trickLaysList.size(); i += 1) {
			res += trickLaysList.get(i).toString() + "\n";
		}
		res += "Remembered Highest Lay = " + highestLay.toString() + "\n";
		res += "Calculated Highest Lay = " + highestLay().toString() + "\n";
		return res;
	}

	public void add(Lay lay) {
		trickLaysList.add(lay);
		if (lay.layCard.rank == 2
			&& lay.layCard.type == Stack.TRUMP) {
			deuceLayer = lay.playerID;
	   } else {
		   catchablePoints += lay.layCard.points;
    	}
    	if (trickLaysList.size() == 1) {
		   highestLay = lay;// fir	st lay is always the highest
    	} else if (highestLay.layCard.type == lay.layCard.type && lay.layCard.rank > highestLay.layCard.rank) { 
		   highestLay = lay;
		} else if (highestLay.layCard.type != Stack.TRUMP && lay.layCard.type == Stack.TRUMP) {
			highestLay = lay;
		}
		if (false && highestLay != highestLay()) {
			System.out.println("problem!");
		}
	}

	public Lay lastLay() {
		if (trickLaysList.size() == 0) {
			return new Lay();
		}
		return trickLaysList.get(trickLaysList.size() - 1);
	}

	public Lay highestLay() {
		//System.out.println("highestLay() executed");
		if (trickLaysList.size() == 0) {
			return new Lay();
		}
		Lay highestLay = trickLaysList.get(0);
		for (Lay lay : trickLaysList) {
			if (lay.layCard.type == Stack.TRUMP
					&& (lay.layCard.rank > highestLay.layCard.rank || highestLay.layCard.type != Stack.TRUMP)) {
				highestLay = lay;
			}
		}
		return highestLay;
	}

	public boolean isCardValid(Card c) {
		// first lay
		if (trickLaysList.size() == 0) return true;
		if (trickLaysList.get(0).layCard.type == Stack.TRUMP && c.type != Stack.TRUMP) return false;
		return true;
	}

	public int capturablePitchPoints() {
		int points = 0;
		for (Lay lay : trickLaysList) {
			// don't count deuce since it can't be captured
			if (lay.layCard.rank!= 2) {
				points += lay.layCard.points;
			}
		}
		return points;
	}
}
