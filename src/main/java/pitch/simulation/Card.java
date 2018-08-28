package pitch.simulation;

public class Card {
	protected int rank;
	protected int suit;
	protected int points;
	protected int type;

	public Card() {
		suit = Stack.NO_SUIT;
		rank = Stack.NO_RANK;
		type = Stack.NO_TRUMP;
		points = Stack.NO_POINT;
	}

	public Card(String input) {
		// s*, b*
		if (input.length() == 2) {
			rank = Stack.rankFromLetter(input.charAt(0));
			suit = Stack.suitFromLetter(input.charAt(1));
		} else {
			rank = Stack.NO_RANK;
			suit = Stack.NO_SUIT;
		}
		type = Stack.NO_TRUMP;
		points = Stack.NO_POINT;
	}

	public Card(int rank, int suit) {
		this.suit = suit;
		this.rank = rank;
		type = Stack.NO_TRUMP;
		points = Stack.NO_POINT;
	}

	public Card(int rank, int suit, int type, int points) {
		this.rank = rank;
		this.suit = suit;
		this.type = type;
		this.points = points;
	}

	public Card getCopy() {
		return new Card(rank, suit, type, points);
	}

	@Override
	public boolean equals(Object oIn) {
		Card o = (Card)oIn;
		return rank == o.rank && suit == o.suit;
	}

	public boolean equalsExactly(Object oIn) {
		Card o = (Card)oIn;
		return rank == o.rank && suit == o.suit && type == o.type && points == o.points;
	}

	public String toString() {
		return Stack.rankToString(rank) + " of " + Stack.suitToString(suit); // + " " +
																// Integer.toString(points);
	}

	public String toLetters() {
		return Stack.rankToLetter(rank) + Stack.suitToLetter(suit);
	}
}
