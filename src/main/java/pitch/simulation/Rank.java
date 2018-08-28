package pitch.simulation;

import java.util.ArrayList;
import java.util.List;

public enum Rank {
	NONE(0, "None", "N"),
	LOW_ACE(1, "Low Ace", "Z"),
	DEUCE(2, "Deuce", "2"),
	THREE(3, "Three spot", "3"),
	FOUR(4, "4", "4"),
	FIVE(5, "5", "5"),
	SIX(6, "6", "6"),
	SEVEN(7, "7", "7"),
	EIGHT(8, "8", "8"),
	NINE(9, "9", "9"),
	TEN(10, "10", "T"),
	SMALL_JOKER(11, "Small Joker", "S"),
	BIG_JOKER(12, "Big Joker", "B"),
	LEFT_JACK(13, "Left Jack", "L"),
	RIGHT_JACK(14, "Right Jack", "R"),
	JACK(15, "Jack", "J"),
	QUEEN(16, "Queen", "Q"),
	KING(17, "King", "K"),
	ACE(18, "Ace", "A");
	
	public final static Rank[] thirteen= { DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE }; // Codes for the 4 suits,

	private int rank;
	private String humanReadable;
	private String oneLetterCode;

	Rank(int rank, String humanReadable, String oneLetterCode) {
		this.rank = rank;
		this.humanReadable = humanReadable;
		this.oneLetterCode = oneLetterCode;
	}
	public int rank() { return rank; }
	public String humanReadable() { return humanReadable; }
	public String input() { return oneLetterCode; }
	public static Rank fromLetter(char letter) {
		switch (Character.toUpperCase(letter)) {
		case 'A': return Rank.ACE;
		case 'K': return Rank.KING;
		case 'Q': return Rank.QUEEN;
		case 'J': return Rank.JACK;
		case 'R': return Rank.RIGHT_JACK;
		case 'L': return Rank.LEFT_JACK;
		case 'B': return Rank.BIG_JOKER;
		case 'S': return Rank.SMALL_JOKER;
		case 'T': return Rank.TEN;
		case '9': return Rank.NINE;
		case '8': return Rank.EIGHT;
		case '7': return Rank.SEVEN;
		case '6': return Rank.SIX;
		case '5': return Rank.FIVE;
		case '4': return Rank.FOUR;
		case '3': return Rank.THREE;
		case '2': return Rank.DEUCE;
		case '1': return Rank.LOW_ACE;
		case '0': return Rank.NONE;
		default: return Rank.NONE;
		}
	}
	public static Rank fromRank(int inputRank) {
		switch (inputRank) {
		case 18: return Rank.ACE;
		case 17: return Rank.KING;
		case 16: return Rank.QUEEN;
		case 15: return Rank.JACK;
		case 14: return Rank.RIGHT_JACK;
		case 13: return Rank.LEFT_JACK;
		case 12: return Rank.BIG_JOKER;
		case 11: return Rank.SMALL_JOKER;
		case 10: return Rank.TEN;
		case 9: return Rank.NINE;
		case 8: return Rank.EIGHT;
		case 7: return Rank.SEVEN;
		case 6: return Rank.SIX;
		case 5: return Rank.FIVE;
		case 4: return Rank.FOUR;
		case 3: return Rank.THREE;
		case 2: return Rank.DEUCE;
		case 1: return Rank.LOW_ACE;
		case 0: return Rank.NONE;
		default: return Rank.NONE;
		}
	}
	public static List<Rank> skipSome() {
		List<Rank> rankList = new ArrayList<Rank>();
		for (Rank rank : Rank.values()) {
			if (rank != Rank.NONE) {
				rankList.add(rank);
			}
		}
		return rankList;
	}
	
}
