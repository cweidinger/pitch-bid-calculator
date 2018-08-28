package pitch.simulation;

import java.util.ArrayList;
import java.util.List;

public class Deal {
	public int dealer;
	public int even_score_change;
	public int odd_score_change;

	public List<Bid> bids;
	public Bid highBid;

	public List<Trick> tricks;

	public Stack outstanding;

	public Deal() {
		dealer = 0;
		even_score_change = 0;
		odd_score_change = 0;

		bids = new ArrayList<Bid>(6);
		highBid = new Bid(-1, 4, Stack.JOKERS);

		tricks = new ArrayList<Trick>(6);
		tricks.add(new Trick());

		outstanding = new CardsStack(16);
		setupOutstanding();
	}

	public void reset() {
		// doesn't work b/c it spills over edge and doesn't wrap back to 0 ---- dealer += 1;
		even_score_change = 0;
		odd_score_change = 0;

		bids.clear();
		highBid = new Bid(-1, 4, Stack.JOKERS);

		tricks.clear();
		tricks.add(new Trick());

		outstanding.clear();
		setupOutstanding();
	}

	public void setupOutstanding() {
		outstanding.addCard(new Card(Stack.DEUCE, Stack.NO_SUIT));                                                                                                           
		outstanding.addCard(new Card(Stack.THREE, Stack.NO_SUIT));                                                                                                           
		outstanding.addCard(new Card(Stack.FOUR, Stack.NO_SUIT));                                                                                                            
		outstanding.addCard(new Card(Stack.FIVE, Stack.NO_SUIT));                                                                                                            
		outstanding.addCard(new Card(Stack.SIX, Stack.NO_SUIT));                                                                                                             
		outstanding.addCard(new Card(Stack.SEVEN, Stack.NO_SUIT));                                                                                                           
		outstanding.addCard(new Card(Stack.EIGHT, Stack.NO_SUIT));                                                                                                           
		outstanding.addCard(new Card(Stack.NINE, Stack.NO_SUIT));                                                                                                            
		outstanding.addCard(new Card(Stack.TEN, Stack.NO_SUIT));                                                                                                             
		outstanding.addCard(new Card(Stack.SMALL_JOKER, Stack.NO_SUIT));                                                                                                     
		outstanding.addCard(new Card(Stack.BIG_JOKER, Stack.NO_SUIT));                                                                                                       
		outstanding.addCard(new Card(Stack.LEFT_JACK, Stack.NO_SUIT));                                                                                                       
		outstanding.addCard(new Card(Stack.RIGHT_JACK, Stack.NO_SUIT));                                                                                                      
		outstanding.addCard(new Card(Stack.QUEEN, Stack.NO_SUIT));                                                                                                           
		outstanding.addCard(new Card(Stack.KING, Stack.NO_SUIT));                                                                                                            
		outstanding.addCard(new Card(Stack.ACE, Stack.NO_SUIT));
	}
	
	public void changeSuitOfOutstandingTo(int suit) {
		int sz = outstanding.size();
		for (int i = 0; i < sz; i++) {
			outstanding.setSuit(i,suit);
		}
	}

	public Trick topTrick() {
		return tricks.get(tricks.size() - 1);
	}

	public Trick topTrickSafely() throws IndexOutOfBoundsException {
		if (tricks.size() == 0) {
			throw new IndexOutOfBoundsException();
		} else {
			return tricks.get(tricks.size() - 1);
		}
	}

	public Bid highestBid() {
		Bid highestBid = new Bid();
		for (int i = 0; i < bids.size(); i++) {
			if (bids.get(i).bid > highestBid.bid) {
				highestBid = bids.get(i);
			}
		}
		return highestBid;
	}

}
