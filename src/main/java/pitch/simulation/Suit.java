package pitch.simulation;

public enum Suit {
	NONE(0,"None", "N"),
	SPADES(1,"Spades", "S"),
	HEARTS(2,"Hearts", "H"),
	CLUBS(3,"Clubs", "C"),
	DIAMONDS(4,"Diamonds", "D"),
	JOKERS(5,"Joker", "*");
	
	private int id;
	private String humanReadable;
	private String oneLetterCode;

	public final static Suit[] four = { SPADES, HEARTS, DIAMONDS, CLUBS }; // Codes for the 4 suits,

	Suit(int id, String humanReadable, String oneLetterCode) {
		this.humanReadable = humanReadable;
		this.id = id;
		this.oneLetterCode = oneLetterCode;
	}
	public static Suit fromHumanReadable(String hr) {
		if (hr == "None") return Suit.NONE;
		else if (hr == "Spades") return Suit.SPADES;
		else if (hr == "Hearts") return Suit.HEARTS;
		else if (hr == "Diamonds") return Suit.DIAMONDS;
		else if (hr == "Clubs") return Suit.CLUBS;
		else if (hr == "Joker") return Suit.JOKERS;
		else return Suit.NONE;
		// switch (hr) {
		// case "None": return Suit.NONE;
		// case "Spades": return Suit.SPADES;
		// case "Hearts": return Suit.HEARTS;
		// case "Diamonds": return Suit.DIAMONDS;
		// case "Clubs": return Suit.CLUBS;
		// case "Joker": return Suit.JOKERS;
		// default: return Suit.NONE;
		// }
	}
	public static Suit fromLetter(char letter) {
		switch (Character.toUpperCase(letter)) {
		case 'N': return Suit.NONE;
		case 'S': return Suit.SPADES;
		case 'H': return Suit.HEARTS;
		case 'D': return Suit.DIAMONDS;
		case 'C': return Suit.CLUBS;
		case '*': return Suit.JOKERS;
		default: return Suit.NONE;
		}
	}
	public static Suit fromId(int inputId) {
		switch (inputId) {
		case 0: return Suit.NONE;
		case 1: return Suit.SPADES;
		case 2: return Suit.HEARTS;
		case 3: return Suit.CLUBS;
		case 4: return Suit.DIAMONDS;
		case 5: return Suit.JOKERS;
		default: return Suit.NONE;
		}
	}
	public static Suit left(Suit trump) {
		switch (trump) {
		case CLUBS:		return Suit.SPADES;
		case SPADES:	return Suit.CLUBS;
		case DIAMONDS:	return Suit.HEARTS;
		case HEARTS:	return Suit.DIAMONDS;
		case JOKERS:	return Suit.JOKERS;
		default:		return Suit.NONE;
		}
	}
	public int id() { return id; }
	public String humanReadable() { return humanReadable; }
	public String input() { return oneLetterCode; }
}

