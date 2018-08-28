package pitch.simulation;

/**
 * @author Clay Weidinger
 * @
 */

import java.util.Random;

public class Player {
	public boolean verbose;
	public Stack hand;
	public String handle;
	public boolean isComputer;
	public int ID;
	public Stack other_peoples_cards;
	boolean three_unlaid_and_not_mine;

	public final static int NONE = -1;

	public boolean getIsComputer() {
		return isComputer;
	}

	public void setIsComputer(boolean newIsComputer) {
		isComputer = newIsComputer;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String newHandle) {
		handle = newHandle;
	}
	
	public Player(String newHandle, int newID, boolean newIsComputer,
			boolean newVerbose) {
		hand = new CardsStack(25);
		handle = newHandle;
		ID = newID; // generateID();
		isComputer = newIsComputer;
		verbose = newVerbose;
		other_peoples_cards = new CardsStack(16);
	}

	public int generateID() {
		int min = 0;
		int max = 100000000;
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}

	public void addCard(Card a) {
		hand.addCard(a);
	}

	public Card lay() {
		if (hand.size() == 0)
			throw new IndexOutOfBoundsException();
		return hand.popCard();
	}

	public Card layCard(boolean highest, boolean lowest, boolean trump,
                        boolean point, boolean nonpoint, boolean three, Card above) {
		// layCard(/*highest*/false, /*lowest*/false, /*trump*/true,
		// /*point*/false, /*nonpoint*/false, /*three*/false, /*above*/new
		// Card());

		// if (hand.size() == 0) { return Card(Card.NOVALUE, Card.NOSUIT); } //
		// should never happen b/c I short circuit this in lay()
		// # if lowest: highest = False # which it already is by default and so
		// it'll rank from lowest to highest
		// #if point == False and nonpoint == False: point = nonpoint = True
		int inc;
		int start;
		int handSize = hand.size();
		if (highest) {
			inc = -1;
			start = handSize - 1;
//			loopOverHand = hand.getCopy();
//			loopOverHand.sortHighToLow();
		} else {
			inc = 1;
			start = 0;
//			loopOverHand = hand;
		}
		// used in python for speed, if (above.value != Card.NONE) { rank_above
		// = above.value; }
		// ?????? INNERLOOP
		Card card;
		int gt = handSize - start;
		for (int i = start; i * inc < gt; i += inc) {
			card = hand.getCard(i);
			// now it's sorted in the caller of this function 
			// System.out.println(card.toString());
			if (above.rank!= Stack.NO_RANK && card.rank <= above.rank) {
				continue;
			}
			if (trump && card.type != Stack.TRUMP) {
				continue;
			}
			if (three == false && card.rank == 3 && card.type == Stack.TRUMP) {
				continue;
			}

			if (card.rank== 2 && card.type == Stack.TRUMP && lowest) {
				hand.removeI(i);
				return card;
			}
			if (point && card.points != 0) {
				hand.removeI(i);
				return card;
			} else if (nonpoint && card.points == 0) {
				hand.removeI(i);
				return card;
			} else if (point == nonpoint) {// # point and nonpoint or (not point
											// and not nonpoint):
				hand.removeI(i);
				return card;
			}
		}
		// couldn't find match with these options so broaden options and try
		// again
		if (point == true) {
			point = false;
		} else if (nonpoint == true) {
			nonpoint = false;
		} else if (above.rank != Stack.NO_RANK) {
			above = new Card();
		} else if (three == false) {
			three = true;
		} else if (trump == true) {
			trump = false;
		}
		return layCard(highest, lowest, trump, point, nonpoint, three, above);
	}


	// player, lays = deal.tricks.get(tricks.size()-1)
	public Lay layFor(Game game) {
		Deal deal = game.deal;
		Trick topTrick = deal.topTrick();

		Lay lay = new Lay();
		lay.playerID = ID;

		// LATER - lead is off suit
		/*
		 * if high card is on your team if high card is likely to remain the
		 * high card if protecting for the 3 Lowest card above Rank - if they
		 * have played and I want to catch it
		 */

		// short circuits
		int num_cards = hand.size();
		if (num_cards == 0) {
			System.err
					.println("THIS SHOULD NEVER HAPPEN: Player.layFor was called but has no cards in hand");
			lay.reason = "I was out last time! This is a programming error!!!!!!!!!!!!!";
			hand.out = true;
			return lay;
		} else if (num_cards == 1) {
			lay.layCard = hand.popCard();
			lay.reason = "Last Card";
			hand.out = true;
			return lay;
		} else if (deal.outstanding.size() == 0) { // there's no more trump to
													// lay so what's the point
													// in logic
			lay.layCard = hand.popCard();
			lay.reason = "No more trump to lay";
			hand.out = true;
			return lay;
		}

		// info gathering
		Card my_high_card = hand.getCard(num_cards - 1);
		// should be presorted to work
		Card high_card = deal.outstanding.getCard(deal.outstanding.size() - 1);
		// System.out.println("High Card = " + high_card.toString());
		// System.out.println("My High Card = " + my_high_card.toString());
		boolean i_have_high_card = high_card.equals(my_high_card);
		// System.out.println(i_have_high_card);

		Lay highestLay = topTrick.highestLay();
		boolean highest_lay_is_high_card = high_card.equals(highestLay.layCard);
		//high_card[Stack.VALUE] == highestLay.layCard.value && high_card[Stack.SUIT] == highestLay.layCard.suit.id();

//		// info gathering
//		int [] my_high_card = hand.getAr(num_cards - 1); 
//		// should be presorted to work
//		int [] high_card = deal.outstanding.getAr(deal.outstanding.size() - 1);
//		// System.out.println("High Card = " + high_card.toString());
//		// System.out.println("My High Card = " + my_high_card.toString());
//		boolean i_have_high_card = Stack.compareArArRankSuit(high_card,my_high_card);
//		// System.out.println(i_have_high_card);
//
//		Lay highestLay = topTrick.highestLay();
//		boolean highest_lay_is_high_card = Stack.compareArCardRankSuit(high_card, highestLay.layCard.suit);
//		//high_card[Stack.VALUE] == highestLay.layCard.value && high_card[Stack.SUIT] == highestLay.layCard.suit.id();

		
		int points_on_table = topTrick.capturablePitchPoints();
		boolean i_can_beat_highest_lay = my_high_card.rank > highestLay.layCard.rank
				|| my_high_card.type == Stack.TRUMP
				&& highestLay.layCard.type == Stack.OFF;

		// lay order
		int laysSize = topTrick.trickLaysList.size();
		boolean first_i_lead = laysSize == 0;
		boolean second_i_follow_lead_my_partner_finishes = laysSize == 1;
		// boolean third_my_partner_led_opponent_finishes = laysSize == 2;
		boolean forth_i_finish = laysSize == 3;

//		boolean three_unlaid_and_not_mine = false;
//		//Stack other_peoples_cards = new CardsStack(16);
//		other_peoples_cards.clear();
//		int outstandingSize = deal.outstanding.size();
//		Card card;
//		for (int i = 0; i < outstandingSize; i++) {
//			card = deal.outstanding.getCard(i);
//			if (!hand.hasCard(card)) {
//				other_peoples_cards.addCard(card);
//				if (card.rank== 3 && card.type == Stack.TRUMP) {
//					three_unlaid_and_not_mine = true;
//				}
//			}
//		}
//		int[] ar;
//		for (int i = 0; i < handSize; i += 1) {
//			ar = deal.outstanding.getAr(i);
//			if (!hand.hasAr(ar)) {
//				other_peoples_cards.addAr(ar);
//				if (ar[Stack.VALUE] == 3 && ar[Stack.TYPE]== Stack.TRUMP) {
//					protect_for_three = true;
//				}
//			}
//		}

		// LOGIC
		if (first_i_lead) {
			if (i_have_high_card) {
				lay.layCard = hand.popCard();
				lay.reason = "I lead and have high card";
			} else {
				lay.layCard = layCard(/* highest */false, /* lowest */true, /* trump */
						true, /* point */false, /* nonpoint */true, /* three */
						false, /* above */new Card());
				lay.reason = "no high card? get out of lead";
			}
		} else if (game.sameTeam(ID, highestLay.playerID)) {
			// if deal.outstanding-my_cards[-1] == highestLay then lay 3
			// if your player lays the highest card, lay the 3 or a point but
			// not the 2 (unless you're trying to protect for the 3 later)
			int opcSize = other_peoples_cards.size();
			if (forth_i_finish) {
				lay.layCard = layCard(/* highest */false, /* lowest */true, /* trump */
						true, /* point */true, /* nonpoint */false, /* three */
						true, /* above */new Card());
				lay.reason = "my teammate has the highest lay and i finish, I want to lay the 3 or a low point";

			} else if (highest_lay_is_high_card
					|| (opcSize >= 1 && other_peoples_cards
							.topCard().equals(highestLay.layCard))) {
				// other logic is highest_lay_is_high_card_after_mine
				lay.layCard = layCard(/* highest */false, /* lowest */true, /* trump */
						true, /* point */true, /* nonpoint */false, /* three */
						true, /* above */new Card());
				lay.reason = "my teammate just laid the highest card other people had so I want to lay the 3 or a point";
			} else if (opcSize >= 2
					&& other_peoples_cards.getCard(
							opcSize - 2).equals(
							highestLay.layCard)) {
				lay.layCard = layCard(/* highest */false, /* lowest */false, /* trump */
						true, /* point */true, /* nonpoint */false, /* three */
						false, /* above */new Card());
				lay.reason = "my team has laid the 2nd highest card outstanding, I want to lay a point but not the 3";
			} else { // ???
				lay.layCard = layCard(/* highest */false, /* lowest */true, /* trump */
						true, /* point */true, /* nonpoint */true, /* three */
						false, /* above */new Card());
				lay.reason = "my teammate has the highest lay but I don't finish and there are 2 other cards that are higher so I'll lay lowest whatever";
			}
			// I don't have to protect for the 3 if my teammate is high
		} else { // highestLayer was an opponent
			if ((points_on_table >= 2 || second_i_follow_lead_my_partner_finishes)
					&& i_have_high_card) {
				Card toReturn = my_high_card.getCopy();
				//hand.removeMyCard(my_high_card);
				hand.removeCard(my_high_card);
				lay.layCard = toReturn;
				lay.reason = "I cant net 2+ points OR 2 people play after me and I have high card";
			} else if (points_on_table >= 3 && i_can_beat_highest_lay) {
				if (forth_i_finish) {
					lay.layCard = layCard(/* highest */false, /* lowest */true, /* trump */
							true, /* point */false, /* nonpoint */false, /* three */
							false, /* above */highestLay.layCard);
					lay.reason = " I'll catch 3+ with the lowest higher card I have since I finish";
				} else {
					lay.layCard = layCard(/* highest */true, /* lowest */false, /* trump */
							true, /* point */false, /* nonpoint */false, /* three */
							false, /* above */new Card());
					lay.reason = "I'll try to catch 3+ with highest card I've got";
				}
			} else if (three_unlaid_and_not_mine && (!forth_i_finish)
					&& i_can_beat_highest_lay) {
				lay.layCard = layCard(/* highest */false, /* lowest */true, /* trump */
						true, /* point */false, /* nonpoint */false, /* three */
						false, /* above */highestLay.layCard);
				lay.reason = "Since I can, I've got to protect for the three";
			} else if (highest_lay_is_high_card) {
				lay.layCard = layCard(/* highest */false, /* lowest */true, /* trump */
						true, /* point */false, /* nonpoint */true, /* three */
						false, /* above */new Card());
				lay.reason = "My opponent laid the high card so I'll go low and try to not lay a point";
			} else if (i_can_beat_highest_lay) {
				lay.layCard = layCard(/* highest */false, /* lowest */true, /* trump */
						true, /* point */false, /* nonpoint */true, /* three */
						false, /* above */new Card());
				lay.reason = "highest layer was opp, I could beat it but I wouldn't even net 2 other points so I'm going low";
			} else {
				lay.layCard = layCard(/* highest */false, /* lowest */true, /* trump */
						true, /* point */false, /* nonpoint */true, /* three */
						false, /* above */new Card());
				lay.reason = "I can't beat it so I won't give up points";
			}
		}
		if (!hand.hasTrump()) hand.out = true; // a sufficiently complicated computer AI might not do this
		return lay;
	}

	public void soplHand(String s) {
		System.out.println("--------------" + handle + "'s hand" + s);
		hand.sopl();
	}

	public void soplHand() {
		soplHand("");
	}

	public boolean out() {
		if (hand.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

}

/*
 * public class LayCardParams { //public boolean highest, lowest, trump, point,
 * nonpoint, three, above; public boolean lowest; public boolean trump; public
 * boolean point; public boolean nonpoint; public boolean ; public boolean
 * highest; public boolean highest; public LayCardParams() { highest = false;
 * lowest = false; trump = true; point = false; nonpoint = false; three = false;
 * above = false; } }
 * 
 * public Card layCard(LayCardParams params) {
 */
