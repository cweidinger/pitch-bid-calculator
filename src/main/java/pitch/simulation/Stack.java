package pitch.simulation;

import java.util.List;

public abstract class Stack {

	public boolean out;

	public final static int RANK = 0;
	public final static int VALUE = 0;
	public final static int SUIT = 1;
	public final static int TYPE= 2;
	public final static int POINTS= 3;

	
	// meaning of ints in array
	public final static int NO_SUIT = 0;
	public final static int SPADES = 1;
	public final static int HEARTS = 2;
	public final static int CLUBS = 3;
	public final static int DIAMONDS = 4;
	public final static int JOKERS = 5;

	public final static int NO_RANK = 0;
	public final static int LOW_ACE = 1;
	public final static int DEUCE = 2;
	public final static int THREE = 3;
	public final static int FOUR = 4;
	public final static int FIVE = 5;
	public final static int SIX = 6;
	public final static int SEVEN = 7;
	public final static int EIGHT = 8;
	public final static int NINE = 9;
	public final static int TEN = 10;
	public final static int SMALL_JOKER = 11;
	public final static int BIG_JOKER = 12;
	public final static int LEFT_JACK = 13;
	public final static int RIGHT_JACK = 14;
	public final static int JACK = 15;
	public final static int QUEEN = 16;
	public final static int KING = 17;
	public final static int ACE = 18;

	public final static int TRUMP = 0;
	public final static int NO_TRUMP = 1;
	public final static int OFF = 2;

	public final static int NO_POINT = 0;
	public final static int ONE_POINT = 1;
	public final static int THREE_POINTS = 3;

	public static int left(int trump) {
		switch (trump) {
		case CLUBS:		return SPADES;
		case SPADES:	return CLUBS;
		case DIAMONDS:	return HEARTS;
		case HEARTS:	return DIAMONDS;
		case JOKERS:	return JOKERS;
		default:		return NO_SUIT;
		}
	}

	public static int rankFromLetter(char letter) {
		switch (Character.toUpperCase(letter)) {
		case 'A': return ACE;
		case 'K': return KING;
		case 'Q': return QUEEN;
		case 'J': return JACK;
		case 'R': return RIGHT_JACK;
		case 'L': return LEFT_JACK;
		case 'B': return BIG_JOKER;
		case 'S': return SMALL_JOKER;
		case 'T': return TEN;
		case '9': return NINE;
		case '8': return EIGHT;
		case '7': return SEVEN;
		case '6': return SIX;
		case '5': return FIVE;
		case '4': return FOUR;
		case '3': return THREE;
		case '2': return DEUCE;
		case '1': return LOW_ACE;
		default:  return NO_RANK; // officially O
		}
	}
	public static String rankToLetter(int rank) {
		switch (rank) {
		case 18: return "A";
		case 17: return "K";
		case 16: return "Q";
		case 15: return "J";
		case 14: return "R";
		case 13: return "L";
		case 12: return "B";
		case 11: return "S";
		case 10: return "T";
		case 9: return "9";
		case 8: return "8";
		case 7: return "7";
		case 6: return "6";
		case 5: return "5";
		case 4: return "4";
		case 3: return "3";
		case 2: return "2";
		case 1: return "1";
		default: return "0";
		}
	}
	public static String rankToString(int rank) {
		switch (rank) {
		case 18: return "Ace";
		case 17: return "King";
		case 16: return "Queen";
		case 15: return "Jack";
		case 14: return "Right Jack";
		case 13: return "Left Jack";
		case 12: return "Big Joker";
		case 11: return "Small Joker";
		case 10: return "10";
		case 9: return "9";
		case 8: return "8";
		case 7: return "7";
		case 6: return "6";
		case 5: return "5";
		case 4: return "4";
		case 3: return "3";
		case 2: return "2";
		case 1: return "Low Ace";
		default: return String.valueOf(rank); // officially 0
		}
	}
	public static String suitToString(int suit) {
		switch (suit) {
		case CLUBS:		return "Clubs";
		case SPADES:	return "Spades";
		case DIAMONDS:	return "Diamonds";
		case HEARTS:	return "Hearts";
		case JOKERS:	return "Jokers";
		default:		return "No Suits";
		}
	}
	public static int suitFromLetter(char letter) {
		switch (Character.toUpperCase(letter)) {
		case 'S': return SPADES;
		case 'H': return HEARTS;
		case 'D': return DIAMONDS;
		case 'C': return CLUBS;
		case '*': return JOKERS;
		default:  return NO_SUIT; // 'N' officially
		}
	}
	public static String suitToLetter(int suit) {
		switch (suit) {
		case CLUBS:		return "C";
		case SPADES:	return "S";
		case DIAMONDS:	return "D";
		case HEARTS:	return "H";
		case JOKERS:	return "J";
		default:		return "N";
		}
	}

	public static Card cardFromAr(int [] ar) { return new Card(ar[0], ar[1], ar[2], ar[3]); }
	public static boolean arEqual(int [] a, int [] b) {
		return (a[0] == b[0] && a[1] == b[1] && a[2] == b[2] && a[3] == b[3]);
	}
	public void transfer(Stack deck) {
		int deckSz = deck.size();
		for (int i = 0; i < deckSz; i += 1) {
			addCard(deck.getCard(i));
		}
		deck.clear();
	}
	public boolean isSameAs(Stack o) {
		//CardsList o = (CardsList) one;
		int mySz = size();
		if (o.size() != mySz)
			return false;
		for (int i = 0; i < mySz; i += 1) {
			if (!o.getCard(i).equalsExactly(getCard(i)))
				return false;
		}
		return true;
	}

	public boolean hasTrump() {
		int k;
		for (k = 0; k < size(); k += 1) {
			if (getCard(k).type == Stack.TRUMP) {
				return true;
			}
		}
		return false;
	}


	public void sopl(String surrounds) {
		System.out.println(surrounds);
		sopl();
		// System.out.println(surrounds);
	}

	public void sopl() {
		String res = "";
		int mySz = size();
		for (int i = 0; i < mySz; i++) {
			// System.out.println(Stack.get(i));
			res += getCard(i) + ", ";
				}
		System.out.println(res);
	}

	public void clone(Stack stackToBeCloned) {
		int stackToBeClonedSize = stackToBeCloned.size();
		clear();
		for (int i = 0; i < stackToBeClonedSize; i++) {
			addAr(stackToBeCloned.getAr(i));
		}
	}
	
	public Card topCard() {
		return getCard(size()-1);
	}

	public void removeCards(Stack o) {
		int osz = o.size();
		int i;
		for (int j = 0; j < osz; j++) {
			i = getIFromAr(o.getAr(j));
			if (i != -1) {
				removeI(i);
			}
		}
	}

	public void addPitchDeck() {
		clear();
		for (Suit suit : Suit.four) {
			for (Rank rank : Rank.thirteen) {
				addCardP(rank.rank(), suit.id(), NO_TRUMP, 0);
			}
		}
		addCardP(BIG_JOKER, JOKERS, NO_TRUMP, 0);
		addCardP(SMALL_JOKER, JOKERS, NO_TRUMP, 0);
	}


	public void cardsFromList(List<String> cardStringList) {
		int rank, suit;
		clear();
		for (String cardString : cardStringList) {
			cardString = cardString.trim().toUpperCase();
			if (cardString.length() == 2) {
				rank = rankFromLetter(cardString.charAt(0));
				suit = suitFromLetter(cardString.charAt(1));
				addCardP(rank, suit, NO_TRUMP, NO_POINT);
			}
		}
	}

	public void burnToSix(int suit, Stack burnt) {
		int i = 0;
		int mySz = size();
		while (i < mySz && mySz > 6) {
			if (getSuit(i) != suit) {
				removeI(i);
				mySz--;
			} else {
				i++;
			}
		}
		sortLowToHigh();
		i = 0;
		// size already set from last while
		while (i < mySz && mySz> 6) {
			if (getPoints(i) == 0) {
				burnt.addAr(getAr(i));
				removeI(i);
				mySz--;
			} else {
				i++;
			}
		}
		if (mySz > 6) {
			i = 0;
			while (i < mySz && mySz > 6) {
				if (getRank(i) != 3) {
					burnt.addAr(getAr(i));
					removeI(i);
					mySz--;
				} else {
					i++;
				}
			}
		}
	}	
	
	
//	public abstract void clone(Stack s);
	
	public abstract void setSuit(int i, int suit);

	public abstract Card getCard(int index);
	public abstract int[] getAr(int i);
	public abstract Stack returnClone();
//	public abstract Card topCard();
//	public abstract Card getCardSafetly(int index) throws IndexOutOfBoundsException;
//	public abstract Card topCardSafely() throws IndexOutOfBoundsException;
//	public abstract Card popCardSafely() throws IndexOutOfBoundsException;

	public abstract int getIFromCard(Card card);
	public abstract int getIFromAr(int [] ar);

	//public abstract void addPitchDeck();	
	public abstract void addCardP(int rank, int suit, int trump, int points);
	public abstract void addAr(int [] a);
	public abstract void addCard(Card a);

	public void removeAr(int[] card) {
		removeI(getIFromAr(card));
	}
	public abstract void removeCard(Card toRemove);
	public abstract void removeRS(int rank, int suit);
	//public abstract void removeRank(int rank);
	//public abstract void removeMyCard(Card my_card);
	public abstract void removeI(int i);
	public abstract Card popCard();
	//public abstract void removeCards(Stack o);

	public boolean hasCard(Card toMatch) {
		return (getIFromCard(toMatch) != -1);
	}
	public abstract boolean hasAr(int [] a);

	public abstract void pitch(int trump, boolean remove);
	//public abstract void burnToSix(int suit, Stack burnt);
	public abstract void trumpifyStack(int trump);

	public abstract int size();
	public abstract void clear();

	public abstract void shuffle();
	public abstract void sortLowToHigh();
	public abstract void sortForBid();

	public abstract int getRank(int i);
	public abstract int getSuit(int i);
	public abstract int getType(int i);
	public abstract int getPoints(int i);

//	public abstract void sopl(String surrounds);
//	public abstract void sopl();

	
}
//public abstract void sortHighToLow();
//public boolean compareArArRankSuit(int[] a, int[] b);
//public boolean compareArCardRankSuit(int[] a, Card c);
