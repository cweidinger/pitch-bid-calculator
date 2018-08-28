/*
  LATER: add looking at whether highest bid is partners or is opponents
 */
package pitch.simulation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BidProfile {
	public Map<Integer, Float> bp;

	public final static int type = 100;
	public final static float CALC = 0;
	public final static float SIMPLE = 1;
	public final static float SCORE = 2;

	public final static int ORIGIN = 101;
	public final static float BUD = 0.0f;
	public final static float CHILD = 1.0f;

	public final static int FITNESS = 102;

	public final static int trump_with_three_weight = 200;
	public final static int at_least_trump_with_three = 201;
	public final static int trump_without_three_weight = 202;
	public final static int at_least_trump_without_three = 203;
	public final static int first_bid_multiplier = 204;
	public final static int range = 205;
	public final static int out_of_deck = 206;

	public BidProfile() {
		bp = new ConcurrentHashMap<Integer, Float>(25);
	}

	public void setFitness(float fitness) {
		bp.put(BidProfile.FITNESS, fitness);
	}

	public float getFitness() {
		return bp.get(BidProfile.FITNESS);
	}

	public BidProfile bud() {
		// randomly choose on value to modify
		BidProfile bud = new BidProfile();
		for (Integer i : bp.keySet()) {
			Float f = 0.0f;
			if (i == ORIGIN) {
				f = BUD;
			} else if (i == FITNESS) {
				f = 0.0f;
			} else {
				Float variance = 1.0f;
				if (((int) (Math.random() * 5)) == 0) { // 20% chance
					if (i > 0) {
						variance = this.bp.get(-1 * i) * (float) Math.random();
						if (((int) (Math.random() * 2)) == 0) {
							f = this.bp.get(i) + variance;
						} else {
							f = this.bp.get(i) - variance;
						}
					} else {
						variance = 0.1f;
						if (((int) (Math.random() * 2)) == 0) {
							f = this.bp.get(i) + variance;
						} else {
							f = this.bp.get(i) - variance;
						}
					}
				} else { // 80% chance of keeping it the same
					f = this.bp.get(i);
				}
			}
			bud.bp.put(i, f);
		}
		return bud;
	}

	public BidProfile mate(BidProfile partner) {
		BidProfile child = new BidProfile();
		for (Integer i : bp.keySet()) {
			Float f = 1.0f;
			if (i == ORIGIN) {
				f = CHILD;
			} else if (i == FITNESS) {
				f = 0.0f;
			} else {
				if (((int) Math.random() * 2) == 0) {
					f = this.bp.get(i);
				} else {
					f = partner.bp.get(i);
				}
			}
			child.bp.put(i, f);
		}
		return child;
	}

	public Bid bid(Player p, Game game) {
		// transform hand for each suit, assign points to each card
		// int team = game.team(p.ID);

		// boolean i_finish = game.deal.bids.size() == 3;
		// boolean i_start = game.deal.bids.size() == 0;
		Bid highestBid = game.deal.highestBid();
		// Bid bestBid = new Bid(p.ID, 0, Card.NONE);
		float bestBidBid = 0;
		Suit bestBidSuit = Suit.NONE;
		float bidBid = 0.0f;
		boolean has_three = false;
		for (Suit suit : Suit.four) {
			has_three = false;
			Stack h = p.hand.returnClone();
			h.pitch(suit.id(), true);
			// bid logic
			bidBid = this.bp.get(out_of_deck); // because you should get
												// something from the deck
			// INNERLOOP
			int size = h.size();
			int cardValue;
			for (int i = 0; i < size; i += 1) {
				cardValue = h.getAr(i)[Stack.VALUE];
				bidBid += this.bp.get(cardValue).floatValue();
				if (cardValue == 3) {
					has_three = true;
					if (h.size() >= this.bp.get(at_least_trump_with_three)
							.intValue()) {
						bidBid += this.bp.get(trump_with_three_weight)
								.floatValue();
					}
				}
			}
			if (!has_three
					&& h.size() >= this.bp.get(at_least_trump_without_three)
							.intValue()) {
				bidBid += this.bp.get(trump_without_three_weight).floatValue();
			}
			// if self.verbose_bid: print(s, bid, h)
			if (bidBid > 10) {
				// System.err.println(" Bid's cannot be more than 10!");
			}
			// System.out.println(bidBid);
			if (bidBid > bestBidBid) {
				bestBidBid = bidBid;
				bestBidSuit = suit;
			}
		}
		// min bid is enforced in the game logic
		if ((int) bestBidBid <= highestBid.bid
				&& ((int) bestBidBid) + this.bp.get(range).intValue() >= highestBid.bid) {
			// my prudent bid wasn't high enough but I'm within range
			bestBidBid = highestBid.bid + 1;
		}
		// if self.verbose_bid: print("best", best_suit, best_bid)
		return new Bid(p.ID, (int) bestBidBid, bestBidSuit.id());
	}

	public void setupDefaults() {
		bp.put(ORIGIN, BUD);
		bp.put(FITNESS, 0.0f);
		bp.put(trump_with_three_weight, 1.0f);
		bp.put(at_least_trump_with_three, 1.0f);
		bp.put(trump_without_three_weight, 1.0f);
		bp.put(at_least_trump_without_three, 1.0f);
		bp.put(first_bid_multiplier, 1.0f);
		bp.put(range, 1.0f);
		bp.put(out_of_deck, 1.0f);
		bp.put(Rank.ACE.rank(), 1.0f);
		bp.put(Rank.KING.rank(), 1.0f);
		bp.put(Rank.QUEEN.rank(), 1.0f);
		bp.put(Rank.RIGHT_JACK.rank(), 1.0f);
		bp.put(Rank.LEFT_JACK.rank(), 1.0f);
		bp.put(Rank.BIG_JOKER.rank(), 1.0f);
		bp.put(Rank.SMALL_JOKER.rank(), 1.0f);
		bp.put(10, 1.0f);
		bp.put(9, 1.0f);
		bp.put(8, 1.0f);
		bp.put(7, 1.0f);
		bp.put(6, 1.0f);
		bp.put(5, 1.0f);
		bp.put(4, 1.0f);
		bp.put(3, 1.0f);
		bp.put(2, 1.0f);
		// setup variances as -1 of the particular number
		// java.util.ConcurrentModificationException
		for (Integer i : bp.keySet()) {
			bp.put(i * -1, 1.5f);
		}
	}

	public Bid getBidFor(int dealer, int playerIndex, Stack hand) {
		Game game = new Game();
		game.verbose = false;
		for (int i = 0; i < 4; i++) {
			game.playersList.add(new Player(Integer.toString(i), i, true,
					game.verbose));
		}

		// BEGIN play(), deal()
		game.evenScoreInt = 0;
		game.oddScoreInt = 0;
		game.deal.dealer = dealer;
		int nextID = game.deal.dealer;
		game.deck.addPitchDeck();
		game.deck.shuffle();
		// ##### remove specific cards from the deck and place into specific
		// persons hand
		if (hand.size() != 9) {
			System.err
					.println(" !!!!!!!!!!!!!!!!!!!!!! BidProfile.getSampleBidFor() you need 9 cards !!!");
		}
		int handSize = hand.size();
		Card card;
		for (int i = 0; i < handSize; i += 1) {
			card = hand.getCard(i);
			game.playersList.get(playerIndex).addCard(card);
			game.deck.removeCard(card);
		}
		// #######
		do {
			nextID = game.nextID(nextID);
			if (nextID != playerIndex) {
				game.playersList.get(nextID).hand.clear(); // / the previous
																// hand should
																// have cleared
																// this
																// up??????????
				for (int j = 0; j < Game.CARDS_DEALT; j++) {
					game.playersList.get(nextID).addCard(game.deck.popCard());
				}
			}
		} while (game.deal.dealer != nextID);
		// END deal()
		// BEGIN bid()
		int thisID = game.deal.dealer;
		Bid highBid = new Bid(-1, 4, Stack.JOKERS);
		Bid lastBid;
		Population pop = new Population(1, 1, 1, 1); // just load up alpha
		BidProfile testBP = pop.getAlpha();
		do {
			thisID = game.nextID(thisID);
			game.deal.bids.add(testBP.bid(game.playersList.get(thisID), game));

			lastBid = game.deal.bids.get(game.deal.bids.size() - 1);
			if (thisID == playerIndex) {
				return lastBid;
			}
			if (lastBid.bid > highBid.bid) { // deal.bids.size() ==
												// players.size()
				highBid = lastBid;
			}
			if (highBid.bid < Game.LOW_BID && game.deal.dealer == thisID) { // force
																			// bid
				lastBid.bid = Game.FORCE_BID;
				highBid = lastBid;
			}
		} while (game.deal.dealer != thisID);
		// END bid();
		return new Bid(-1, 4, Stack.JOKERS);
	}
}
