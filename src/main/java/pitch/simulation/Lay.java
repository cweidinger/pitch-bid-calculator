package pitch.simulation;

public class Lay {
	public int playerID;
	public Card layCard;
	public String reason;

	public Lay() {
		playerID = Player.NONE;
		layCard = new Card();
		reason = "";
	}

	public Lay(int newPlayerID, Card newCard, String newReason) {
		playerID = newPlayerID;
		layCard = newCard;
		reason = newReason;
	}

	public String toString() {
		return "Player " + playerID + " laid " + layCard.toLetters() + " b/c " + reason;
	}
}
