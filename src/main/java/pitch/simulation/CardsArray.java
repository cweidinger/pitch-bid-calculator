package pitch.simulation;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CardsArray extends Stack {
	public CardsArray(int size) { initializeStore(size); }
	public CardsArray(String commaSeperatedCardString) {
		List<String> cardStringList = Arrays.asList(commaSeperatedCardString
				.split(","));
		initializeStore(cardStringList.size());
		cardsFromList(cardStringList);
	}

	public int[][] ars;
	public int sz;
	private int initSz;
	private void initializeStore(int size) {
		ars = new int[size][4];
		initSz = size;
		sz = 0;
	}


	private boolean trumpifyCard(int [] a, int gameTrumpSuitId) {
		a[TYPE] = TRUMP;
		if (JACK == a[RANK]) {
			if (a[SUIT] == gameTrumpSuitId) {
				a[RANK] = RIGHT_JACK;
			} else if (a[SUIT] == left(gameTrumpSuitId)) {
				a[RANK]= LEFT_JACK;
				a[SUIT] = gameTrumpSuitId;
			} else {
				a[TYPE] = OFF;
			}
		} else if (BIG_JOKER == a[RANK] || SMALL_JOKER == a[RANK]) {
			a[SUIT] = gameTrumpSuitId;
		} else if (a[SUIT] != gameTrumpSuitId) {
			a[TYPE] = OFF;
		}
		a[POINTS] = pitchPointValue(a);
		return (a[SUIT] == gameTrumpSuitId); 
	}

	private static int pitchPointValue(int[] a) {
		if (a[TYPE] == OFF) {
			return 0;
		}
		if ((a[RANK] < 10 && a[RANK]> 3) || a[RANK] == KING || a[RANK] == QUEEN) {
			return 0;
		} else if (a[RANK] == 3) {
			return 3;
		} else {
			return 1;
		}
	}

	@Override
	public void trumpifyStack(int trump) {
		for (int i = 0; i < sz; i++) {
			trumpifyCard(ars[i], trump);
		}
	}

	@Override
	public void clear() { sz = 0; }

	@Override
	public int size() { return sz; }

	@Override
	public int[] getAr(int i) { return ars[i]; }

	@Override
	public Card getCard(int i) {
		//return cardFromAr(getAr(i));
		return new Card(ars[i][RANK], ars[i][SUIT], ars[i][TYPE], ars[i][POINTS]);
	}

	@Override
	public int getIFromCard(Card card) {
		int[] ar = { card.rank, card.suit, 0, 0 };
		return getIFromAr(ar);
	}

	@Override
	public void addCard(Card card) {
		int[] ar = {card.rank, card.suit, card.type, card.points};
		addAr(ar);
	}

	@Override
	public void addAr(int[] ar) {
		if (sz == initSz) {
			System.out.println("Someone is trying to add too many cards to the CardsArray class");
		} else {
			ars[sz++] = ar;
		}
	}
	

	@Override
	public int getIFromAr(int[] card) {
		for (int i = sz-1; i >= 0; --i) {
			if (card[0] == ars[i][0] && card[1] == ars[i][1]) {
				return i;
			}
		}
		return -1;
	}

//	@Override
//	public int getIFromRank(int rank) {
//		for (int i = sz-1; i >= 0; --i) {
//			if (rank == ars[i][RANK]) {
//				return i;
//			}
//		}
//		return -1;
//	}
//
	@Override
	public void removeI(int i) {
		for (; i < sz - 1; i++) {
			ars[i] = ars[i+1];
		}
		ars[sz-1] = new int[] {0,0,0,0};
		sz--;
	}

	@Override
	public void removeCard(Card card) {
		int[] ar = {card.rank, card.suit,0,0}; 
		this.removeAr(ar);
	}

	@Override
	public void setSuit(int i, int suit) {
		getAr(i)[SUIT] = suit;
//		int[] a = getAr(i);
//		a[SUIT] = suit;
	}

	
	@Override
	public void addCardP(int rank, int suit, int trump, int points) {
		int [] a = {rank, suit, trump, points};
		addAr(a);
	}
	
	
	@Override
	public CardsArray returnClone() {
		CardsArray copy = new CardsArray(sz);
		int [][] arsCopy = copy.ars;
		for (int i = 0; i < sz; i += 1) {
			arsCopy[i] = ars[i];
		}
		copy.sz = sz;
		return copy;
	}

	@Override
	public boolean hasCard(Card card) {
		int[] a = {card.rank, card.suit,0,0};
		return (getIFromAr(a) != -1);
	}

	@Override
	public boolean hasAr(int [] a) {
		return (getIFromAr(a) != -1);
	}

	@Override
	public void pitch(int trump, boolean remove) {
		for (int i = 0; i < sz;) {
			if (trumpifyCard(ars[i], trump) || !remove) { // must execute trumpify
				i++;
			} else {
				removeI(i);
			}
		}
	}

	// =================== OVERRIDING TO OPTIMIZE ===============================
	@Override
	public void transfer(Stack input) {
		if (input instanceof CardsArray) {
			CardsArray deck = (CardsArray)input;
			int deckSz = deck.size();
			for (int i = 0; i < deckSz; i += 1) {
				addAr(deck.getAr(i));
			}
			deck.sz = 0;
		} else {
			super.transfer(input);
		}
	}

	public void burnToSix(int suit, Stack burnt) {
		int i = 0;
		while (i < sz && sz > 6) {
			if (ars[i][SUIT] != suit) {
				removeI(i);
			} else {
				i++;
			}
		}
		sortLowToHigh();
		i = 0;
		// size already set from last while
		while (i < sz && sz> 6) {
			if (ars[i][POINTS] == 0) {
				burnt.addAr(ars[i]);
				removeI(i);
			} else {
				i++;
			}
		}
		if (sz > 6) {
			i = 0;
			while (i < sz && sz > 6) {
				if (ars[i][RANK] != 3) {
					burnt.addAr(ars[i]);
					removeI(i);
				} else {
					i++;
				}
			}
		}
	}	
	
//	public boolean isSameAs(Stack one) {
//		CardsArray o = (CardsArray) one;
//		if (o.size() != sz)
//			return false;
//		int[][] oar = o.getArAr();
//		for (int i = 0; i < sz; i += 1) {
//			if (ars[i][RANK] != oar[i][RANK] || ars[i][SUIT] != oar[i][SUIT]
//					|| ars[i][TYPE] != oar[i][TYPE]
//					|| ars[i][POINTS] != oar[i][POINTS])
//				return false;
//		}
//		return true;
//	}
	
	// =================== INLINING OPTIMIZATIONS ===============================
	// =================== PRIMATIVES OF STORE ===============================

	public int [] popAr() {
		int lastIndex = sz -1;
		int [] c = ars[lastIndex];
		removeI(lastIndex);
		return c;
	}
	public Card popCard() {	return cardFromAr(popAr()); }
	
	public int getRank(int i) { return getAr(i)[RANK]; }
	public int getSuit(int i) { return getAr(i)[SUIT]; }
	public int getType(int i) { return getAr(i)[TYPE]; }
	public int getPoints(int i) { return getAr(i)[POINTS]; }

	// =================== IMPLEMENTATION DETAILS =================================
	public void shuffle() {
		long seed = System.nanoTime();
		Random rnd = new Random(seed);
//		List<Integer> indexes = new ArrayList<Integer>(sz);
//		for (int i = 0; i < sz; i++) {
//			indexes.add(i);
//		}
//		Collections.shuffle(indexes, rnd);
//		int [][] rars = new int[sz][4];
//		for (int i = 0; i < sz; i++) {
//			rars[i] = ars[indexes.get(i)];
//		}
//		ars = rars;

//		oldway
		for (int i = sz - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int [] a = ars[index];
			ars[index] = ars[i];
			ars[i] = a;
		}
	}

	@Override
	public void sortLowToHigh() { // Low to High
		Arrays.sort(ars, 0, sz, new Comparator<int []>() {
			public int compare(int [] a, int [] b) {
				return (a[TYPE] == OFF && b[TYPE] == TRUMP || a[TYPE] == b[TYPE] && a[RANK] < b[RANK]) ? -1
				     : 1; /*(b[TYPE] == OFF && a[TYPE] == TRUMP || a[TYPE] == b[TYPE] && a[RANK] > b[RANK]) ? 1
								: 0;*/
			}
		});
	}

	@Override
	public void sortForBid() { // Low to High
		Arrays.sort(ars, 0, sz, new Comparator<int []>() {
			public int compare(int [] a, int [] b) {
				return (a[SUIT] < b[SUIT] || a[SUIT] == b[SUIT] && a[RANK] < b[RANK]) ? -1 : 1;
			}
		});
	}
	
	@Override
	public void removeRS(int rank, int suit) {
		int [] ar;
		for (int i = 0; i < sz; i++) {
			ar = ars[i];
			if (ar[RANK] == rank && ar[SUIT] == suit) {
				removeI(i);
				return;
			}
		}
	}
}
