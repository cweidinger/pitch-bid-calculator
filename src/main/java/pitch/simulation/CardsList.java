package pitch.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CardsList extends Stack {

	private List<Card> list;
	private void initializeStore(int size) { list = new ArrayList<Card>(); out = false; }

	public CardsList(int size) { initializeStore(size); }
	public CardsList(String commaSeperatedCardString) {
		List<String> cardStringList = Arrays.asList(commaSeperatedCardString
				.split(","));
		initializeStore(cardStringList.size());
		cardsFromList(cardStringList);
	}


	
	private boolean trumpifyI(int i, int gameTrump) {
		Card card = list.get(i);
		card.type = Stack.TRUMP;
		if (Stack.JACK == card.rank) {
			if (card.suit == gameTrump) {
				card.rank = Stack.RIGHT_JACK;
			} else if (card.suit == Stack.left(gameTrump)) {
				card.rank = Stack.LEFT_JACK;
				card.suit = gameTrump;
			} else {
				card.type = Stack.OFF;
			}
		} else if (Stack.BIG_JOKER == card.rank
				|| Stack.SMALL_JOKER == card.rank) {
			card.suit = gameTrump;
		} else if (card.suit != gameTrump) {
			card.type = Stack.OFF;
		}
		card.points = pitchPointValue(card);
		return (card.type == Stack.TRUMP);
	}

	@Override
	public void pitch(int trump, boolean remove) {
		int mySz = list.size();
		int i = 0;
		if (remove) {
			while (i < mySz) {
				if (trumpifyI(i, trump)) {
					i++;
				} else {
					list.remove(i);
					mySz--;
				}
			}
		} else {
			while (i < mySz) {
				trumpifyI(i, trump);
				i++;
			}
		}
//		while (i < mySz) {
//			if (trumpifyI(i, trump) || !remove) {
//				i++;
//			} else {
//				removeI(i);
//			}
//		}
	}

	@Override
	public void trumpifyStack(int trump) {
		int sz = size();
		for (int i = 0; i < sz; i++) {
			trumpifyI(i, trump);
		}
	}

	
	
	private int pitchPointValue(Card c) {
		if (c.type == Stack.OFF) {
			return 0;
		}
		if ((c.rank < 10 && c.rank > 3) || c.rank == Stack.KING
				|| c.rank == Stack.QUEEN) {
			return 0;
		} else if (c.rank == 3) {
			return 3;
		} else {
			return 1;
		}
	}
	
	
	
	//	@Override
//	public void addPitchDeck() {
//		for (Suit suit : Suit.four) {
//			for (Rank rank : Rank.thirteen) {
//				list.add(new Card(rank.rank(), suit.id()));
//			}
//		}
//		list.add(new Card(Stack.BIG_JOKER, Stack.JOKERS));
//		list.add(new Card(Stack.SMALL_JOKER, Stack.JOKERS));
//	}
//

	@Override
	public CardsList returnClone() {
		CardsList copy = new CardsList(9);
		int size = list.size();
//		Card card;
		for (int i = 0; i < size; i += 1) {
//			card = list.get(i);
//			copy.list.add(new Card(card.rank, card.suit, card.type, card.points));
			copy.list.add(list.get(i).getCopy());
			copy.out = false;
		}
		return copy;
	}

	@Override
	public void addCard(Card a) {
		list.add(a);
		out = false;
	}

	@Override
	public void addAr(int[] a) {
		list.add(cardFromAr(a));
		out = false;
	}

	@Override
	public void setSuit(int i, int suit) {
		list.get(i).suit = suit;
	}

	
	@Override
	public int getIFromCard(Card card) {
		int mySz = list.size();
		Card c;
		for (int i = 0; i < mySz; i++) {
			c = list.get(i);
			//if (list.get(i).equals(card)) {
			if (c.rank == card.rank && c.suit == card.suit) {
				return i;
			}
		}
		return -1;
	}
	@Override
	public int getIFromAr(int[] card) {
		return getIFromCard(new Card(card[RANK], card[SUIT]));
	}

	@Override
	public void removeCards(Stack o) {
		int oSz = o.size();
		int mySz = size();
		Card toRemove, card;
		for (int j = 0; j < oSz; j++) {
//			removeCard(o.getCard(j));
			toRemove = o.getCard(j);
			for (int i = 0; i < mySz; i++) {
				card = list.get(i);
				//if (list.get(i).equals(toRemove)) {
				if (card.rank == toRemove.rank && card.suit == toRemove.suit) {
					list.remove(i);
					mySz--;
				}
			}
		}
	}

	@Override
	public void removeCard(Card toRemove) {
		//removeI(getIFromCard(toRemove));
		int mySz = size();
		for (int i = 0; i < mySz; i++) {
			if (list.get(i).equals(toRemove)) {
				list.remove(i);
				return;
			}
		}
	}

//	@Override
//	public void removeRank(int rank) {
//		for (int i = 0; i < list.size(); i++) {
//			if (list.get(i).rank == rank) {
//				list.remove(i);
//			}
//		}
//	}
//
//	@Override
//	public void removeMyCard(Card my_card) {
//		list.remove(my_card);
//	}

	@Override
	public void removeI(int i) {
		list.remove(i);
	}

	@Override
	public Card popCard() {
		int lastIndex = list.size() - 1;
		Card c = list.get(lastIndex);
		list.remove(lastIndex);
		return c;
	}

	@Override
	public final Card getCard(int i) {
		return list.get(i);
	}

	@Override
	public int[] getAr(int i) {
		Card c = list.get(i);
		return new int[] { c.rank, c.suit, c.type, c.points };
	}


	@Override
	public boolean hasAr(int[] a) {
		int size = list.size();
		Card c;
		for (int i = 0; i < size; i++) {
			c = list.get(i);
			if (c.rank == a[VALUE] && c.suit == a[SUIT]) {
				return true;
			}
		}
		return false;
	}




	// =================== OVERRIDING FOR OPTIMIZATION  ===============================

	@Override
	public void clone(Stack stackToBeCloned) {
		int stackToBeClonedSize = stackToBeCloned.size();
		list.clear();
		for (int i = 0; i < stackToBeClonedSize; i++) {
			list.add(stackToBeCloned.getCard(i));
			out = false;
		}
	}
	
	@Override
	public boolean hasCard(Card toMatch) {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			if (list.get(i).equals(toMatch)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void burnToSix(int suit, Stack burnt) {
		int i = 0;
		int size = list.size();
		while (i < size && size > 6) {
			if (list.get(i).suit != suit) {
				list.remove(i);
				size--;
			} else {
				i++;
			}
		}
		sortLowToHigh();
		// size already set from last while
		Card card;
		i = 0;
		while (i < size && size > 6) {
			card = list.get(i);
			if (card.points == 0) {
				burnt.addCard(card.getCopy());
				list.remove(i);
				size--;
			} else {
				i++;
			}
		}
		// if size is still to big then I'll have to burn some points but not
		// the 3????
		if (size > 6) {
			i = 0;
			while (i < size && size > 6) {
				card = list.get(i);
				if (card.rank != 3) {
					burnt.addCard(card.getCopy());
					list.remove(i);
					size--;
				} else {
					i++;
				}
			}
		}
	}

	@Override
	public Card topCard() {	return list.get(list.size() - 1); }
	
// =================== INLINING OPTIMIZATIONS ===============================
	// =================== PRIMATIVES OF STORE ===============================
	@Override
	public void addCardP(int rank, int suit, int trump, int points) {
		list.add(new Card(rank, suit, trump, points));
		out = false;
	}


	
	@Override
	public int size() {	return list.size();	}
	@Override
	public void clear() { this.list.clear(); }
	@Override
	public int getRank(int i) { return list.get(i).rank; }
	@Override
	public int getSuit(int i) { return list.get(i).suit; }
	@Override
	public int getType(int i) { return list.get(i).type; }
	@Override
	public int getPoints(int i) { return list.get(i).points; }

	
	
	// =================== IMPLEMENTATION DETAILS =============
	@Override
	public void shuffle() { Collections.shuffle(list, new Random(System.nanoTime())); }

	@Override
	public void sortLowToHigh() { // Low to High
		Collections.sort(list, new Comparator<Card>() {
			public int compare(Card a, Card b) {
				return (a.type == OFF && b.type == TRUMP || a.type == b.type && a.rank < b.rank) ? -1 
						: 1; 
						// I don't need to check them b/c they should never have 2 of the same card
						/*(b.type == OFF && a.type == TRUMP || a.type == b.type	&& a.rank > b.rank) ? 1
								: 0;*/
			}
		});
	}

	@Override
	public void sortForBid() { // Low to High
		Collections.sort(list, new Comparator<Card>() {
			public int compare(Card a, Card b) {
				return (a.suit < b.suit || a.suit == b.suit && a.rank < b.rank) ? -1 : 1;
			}
		});
	}

	@Override
	public void removeRS(int rank, int suit) {
		int mySz = list.size();
		Card card;
		for (int i = 0; i < mySz; i++) {
			card = list.get(i);
			if (card.rank == rank && card.suit == suit) {
				list.remove(i);
				return;
			}
		}
	}
	public String toCommaSeperatedString() {
		String out = "";
		Card card;
		boolean first = true;
		int sz = size();
		for (int i = 0; i < sz; i++) {
			card = list.get(i);
			if (first) {
				out = Stack.rankToLetter(card.rank) + Stack.suitToLetter(card.suit);
				first = false;
			} else {
				out += "," + Stack.rankToLetter(card.rank) + Stack.suitToLetter(card.suit);
				
			}
		}
		return out;
	}


	
}
