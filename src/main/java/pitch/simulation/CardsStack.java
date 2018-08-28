package pitch.simulation;

//public class CardsStack extends Stack {
//	public Stack cl;
//	public Stack clo;
//	public Stack ca;
//	public Stack other;
//	public Stack originalSetOfCards;
//
//	public CardsStack(String commaSeperatedCardString) {
//		cl = new CardsList(commaSeperatedCardString);
//		clo = new CardsList(commaSeperatedCardString);
//		ca = new CardsArray(commaSeperatedCardString);
//		other = new CardsArray(commaSeperatedCardString);
//	}
//
//	public CardsStack(int size) {
//		cl = new CardsList(size);
//		clo = new CardsList(size);
//		ca = new CardsArray(size);
//		other = new CardsArray(size);
//	}
//		
//	public void burnToSix(int suit, Stack burnt) {
//		other.clone(burnt);
//		cl.burnToSix(suit, burnt);
//		ca.burnToSix(suit, other);
//		if (!ca.isSameAs(cl)) {
//			System.out.println("pitch conflict in burnToSix");
//		}
//		if (!other.isSameAs(burnt)) {
//			System.out.println("burnToSix conflict");
//		}
//	}	
//	
//	public void pitch(int trump, boolean remove) {
//		cl.pitch(trump, remove);
//		ca.pitch(trump, remove);
//		if (!ca.isSameAs(cl)) {
//			System.out.println("pitch conflict");
//		}
//	}
//	
//	
//	public void removeCard(Card c) {
//		ca.removeCard(c);
//		cl.removeCard(c);
//		if (!ca.isSameAs(cl)) {
//			System.out.println("removeCard conflict");
//		}
//	}
//	
//	
//	public void addAr(int[] c) {
//		ca.addAr(c);
//		cl.addAr(c);
//		if (!ca.isSameAs(cl)) {
//			System.out.println("addAr conflict");
//		}
//	}
//	
//	
//	public void addCard(Card c) {
//		ca.addCard(c);
//		cl.addCard(c);
//		if (!ca.isSameAs(cl)) {
//			System.out.println("addCard conflict");
//		}
//	}
//
//	
//	public void clear() {
//		ca.clear();
//		cl.clear();
//		if (!ca.isSameAs(cl)) {
//			System.out.println("clear conflict");
//		}
//	}
//	
//	
//	public void clone(Stack s) {
//		ca.clone(s);
//		cl.clone(s);
//		if (!ca.isSameAs(cl)) {
//			System.out.println("clone conflict");
//		}
//	}
//	
//	
//	public int[] getAr(int i) {
//		int[] s = ca.getAr(i);
//		int[] r = cl.getAr(i);
//		if (!ca.isSameAs(cl)) {
//			System.out.println("getAr stack not same");
//		}
//		if (s[0] != r[0] || s[1] != r[1] || s[2] != r[2] || s[3] != r[3]) {
//			System.out.println("getAr array not same");
//		}
//		return r;
//	}
//
//	
//	public Card getCard(int i) {
//		Card s = ca.getCard(i);
//		Card r = cl.getCard(i);
////		if (!co.isSameAs(cl)) {
////			System.out.println("getCard stack not same");
////		}
//		if (!s.equals(r)) {
//			System.out.println("getCard card not same");
//		}
//		return r;
//	}
//
//	
//	public Stack getCopy() {
//		Stack r = ca.getCopy();
//		Stack s = cl.getCopy();
//		if (!ca.isSameAs(cl)) {
//			System.out.println("getCopy originals not the same");
//		}
//		if (!r.isSameAs(s)) {
//			System.out.println("getCopy copies not the same");
//		}
//		return s;
//	}
//	
//	
//	public boolean hasAr(int[] a) {
//		boolean s= ca.hasAr(a);
//		boolean r = cl.hasAr(a);
//		if (!ca.isSameAs(cl)) {
//			System.out.println("hasAr stack not same");
//		}
//		if (s != r) {
//			System.out.println("hasAr not same");
//		}
//		return r;
//	}
//
//	
//	public Card popCard() {
//		Card s= ca.popCard();
//		Card r = cl.popCard();
//		if (!ca.isSameAs(cl)) {
//			System.out.println("popCard stack not same");
//		}
//		if (!s.equals(r)) {
//			System.out.println("popCard not same");
//		}
//		return r;
//	}
//
//	
//	public void removeCards(Stack stack) {
//		if (!ca.isSameAs(cl)) {
//			System.out.println("removeCards stack not same");
//		}
//		ca.removeCards(stack);
//		cl.removeCards(stack);
//		if (!ca.isSameAs(cl)) {
//			System.out.println("removeCards stack not same");
//		}
//	}
//
//	
//	public void removeI(int i) {
//		if (ca.isSameAs(cl)) {
//			ca.removeI(i);
//		}
//		cl.removeI(i);
//		if (!ca.isSameAs(cl)) {
//			System.out.println("removeI stack not same");
//		}
//	}
//
//	
////	public void removeMyCard(Card c) {
////		ca.removeMyCard(c);
////		cl.removeMyCard(c);
////		if (!ca.isSameAs(cl)) {
////			System.out.println("removeMyCard stack not same");
////		}
////	}
////
////	
////	public void removeRank(int rank) {
////		ca.removeRank(rank);
////		cl.removeRank(rank);
////		if (!ca.isSameAs(cl)) {
////			System.out.println("removeRank stack not same");
////		}
////	}
//
//	
//	public void shuffle() {
//		//co.shuffle();
//		cl.shuffle();
//		ca.clone(cl);
//		if (!ca.isSameAs(cl)) {
//			System.out.println("shuffle stack not same");
//		}
//	}
//
//	
//	public int size() {
//		int r = ca.size();
//		int s = cl.size();
////		if (!co.isSameAs(cl)) {
////			System.out.println("size stack not same");
////		}
//		if (r != s) {
//			System.out.println("size not same");
//		}
//		return s;
//	}
//
//	
//	public void sortLowToHigh() {
//		ca.sortLowToHigh();
//		cl.sortLowToHigh();
//		if (!ca.isSameAs(cl)) {
//			System.out.println("sort stack not same");
//		}
//	}
//
//	
//	public Card topCard() {
//		Card r = ca.topCard();
//		Card s = cl.topCard();
//		if (!ca.isSameAs(cl)) {
//			System.out.println("topCard stack not same");
//		}
//		if (!r.equals(s)) {
//			System.out.println("topCard Card not same");
//		}
//		return s;
//	}
//
//	
//	public void transfer(Stack toTransfer) {
//		Stack otherToTransfer = toTransfer.getCopy();
//		ca.transfer(otherToTransfer);
//		cl.transfer(toTransfer);
//		if (!ca.isSameAs(cl)) {
//			System.out.println("transfer stack not same");
//		}
//		if (!toTransfer.isSameAs(otherToTransfer)) {
//			System.out.println("transfered stack not same");
//		}
//	}
//	
//	
//	public void addPitchDeck() {
//		cl.addPitchDeck();
//		ca.addPitchDeck();
//		if (!ca.isSameAs(cl)) {
//			System.out.println("addPitchDeck stack not same");
//		}
//	}
//
//	@Override
//	public int getIFromCard(Card card) {
//		int b = ca.getIFromCard(card);
//		int a = cl.getIFromCard(card);
//		if (a != b) {
//			System.out.println("getIFromCard isn't the same");
//		}
//		return a;
//	}
//
//	@Override
//	public boolean hasCard(Card toMatch) {
//		boolean a = cl.hasCard(toMatch);
//		boolean b = ca.hasCard(toMatch);
//		if (a != b) {
//			System.out.println("hasCard isn't the same");
//		}
//		return a;
//	}
//
//	@Override
//	public void trumpifyStack(int trump) {
//		cl.trumpifyStack(trump);
//		ca.trumpifyStack(trump);
//	}
//
////	@Override
////	public void sortHighToLow() {
////		ca.sortHighToLow();
////		cl.sortHighToLow();
////	}
//
//	@Override
//	public void sopl(String surrounds) {
//		cl.sopl(surrounds);
//		ca.sopl(surrounds);
//	}
//
//	@Override
//	public void sopl() {
//		cl.sopl();
//		ca.sopl();
//	}
//
//	@Override
//	public void setSuit(int i, int suit) {
//		cl.setSuit(i, suit);
//		ca.setSuit(i, suit);
//	}
//
//	@Override
//	public int getIFromAr(int[] ar) {
//		int r = cl.getIFromAr(ar);
//		int s = ca.getIFromAr(ar);
//		if (r != s) {
//			System.out.println("getIFromAr isn't the same");
//		}
//		return s;
//	}
//
//	@Override
//	public void addCardP(int rank, int suit, int trump, int points) {
//		cl.addCardP(rank,suit,trump,points);
//		ca.addCardP(rank,suit,trump,points);
//		if (!cl.isSameAs(ca)) {
//			System.out.println("addCardP stack isn't the same");
//		}
//	}
//
//	@Override
//	public int getRank(int i) {
//		int r = cl.getRank(i);
//		int s = ca.getRank(i);
//		if (r != s) {
//			System.out.println("getRank isn't the same");
//		}
//		return s;
//	}
//
//	@Override
//	public int getSuit(int i) {
//		int r = cl.getSuit(i);
//		int s = ca.getSuit(i);
//		if (r != s) {
//			System.out.println("getSuit isn't the same");
//		}
//		return s;
//	}
//
//	@Override
//	public int getType(int i) {
//		int r = cl.getType(i);
//		int s = ca.getType(i);
//		if (r != s) {
//			System.out.println("getType isn't the same");
//		}
//		return s;
//	}
//
//	@Override
//	public int getPoints(int i) {
//		int r = cl.getPoints(i);
//		int s = ca.getPoints(i);
//		if (r != s) {
//			System.out.println("getPoints isn't the same");
//		}
//		return s;
//	}
//}
/*
specific to CardsList
pitchPointValue(Card)	100.0 %	32	0	32
trumpifyCard(Card, int)	100.0 %	56	0	56

only called by constructor
initializeList(int)	100.0 %	6	0	6
 */

public class CardsStack extends CardsList {
	public CardsStack(String commaSeperatedCardString) {
		super(commaSeperatedCardString);
	}
	public CardsStack(int size) {
		super(size);
	}
}
