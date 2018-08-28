package pitch.simulation;

public class Bid {

	public static final int PASS = 1;
	public int playerID;
	public int bid;
	public int suit;

	public Bid() {
		playerID = Player.NONE;
		bid = 0;
		suit = Stack.NO_SUIT;
	}

	public Bid(int newPlayerID, int newBid, int newSuit) {
		playerID = newPlayerID;
		bid = newBid;
		suit = newSuit;
	}
}
