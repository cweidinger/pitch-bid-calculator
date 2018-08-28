package pitch.simulation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
	public static final long RANDOM_SEED = -1; // if -1 then use actually random
												// number
	public static final int DEALS_BEFORE_ENDING = 12; // needs to be divisible
														// by 4 and 6 so that
														// everyone gets an
														// equal chance to bid
	public static final int WINNING_SCORE = 31;
	public static final int FORCE_BID = 4;
	public static final int LOW_BID = 5;
	public static final int CARDS_DEALT = 9;
	public static final int EVEN = 0;
	public static final int ODD = 1;

	// public static final int BID = 0;
	// public static final int BURN = 1;
	// public static final int LAY = 2;
	// public static final int BID = 3;
	//public static final int BID = 4;
	public String current_action; // bid, burn,lay
	public int current_player_index;

	public List<Motion> motions;

	public int moreThanSixInt;
	public int nPlayers;
	public List<Player> playersList;
	public List<Stack> handsList;
	public Stack deck;
	// Cards discard;
	public Deal deal;
	public List<Deal> dealList;
	public int evenScoreInt;
	public int oddScoreInt;
	public boolean verbose;
	public Stack burntByPlayer;
	public Stack burnt;

	public boolean bidFunc;
	public BidProfile evenBidProfile;
	public BidProfile oddBidProfile;

	public List<String> events;

	public Game() {
		motions = new ArrayList<Motion>(10);
		events = new ArrayList<String>(10);
		nPlayers = 4;
		verbose = false;
		playersList = new ArrayList<Player>(6);
		boolean isComputer = true;
      for (int i = 0; i < nPlayers; i++) {
        playersList.add(new Player("Computer " + Integer.toString(i), i, isComputer, verbose));
      }
		burntByPlayer = new CardsStack(6);
		burnt = new CardsStack(6);
		deck = new CardsStack(54);
		deal = new Deal();
		reset();
		bidFunc = false;
	}

	public void reset() {
		burnt.clear();
		burntByPlayer.clear();
		deal.reset();
		dealList = new ArrayList<Deal>(DEALS_BEFORE_ENDING);
		moreThanSixInt = 0;
		evenScoreInt = 0;
		oddScoreInt = 0;
		current_action = "newDeal";
	}

	public int nextID(int currentID) {
		currentID++;
		if (currentID == playersList.size()) {
			currentID = 0;
		}
		return currentID;
	}

	public int team(int p1) {
		return p1 % 2;
	}

	public boolean sameTeam(int p1, int p2) {
		if (p1 % 2 == p2 % 2) {
			return true;
		} else {
			return false;
		}
	}

	public int play() {
		evenScoreInt = 0;
		oddScoreInt = 0;
		int dealer = 1;
		deal.dealer = dealer;
		while (((evenScoreInt <= 31 && oddScoreInt <= 31) || evenScoreInt == oddScoreInt)
				&& dealList.size() < DEALS_BEFORE_ENDING) {
			if (verbose) {
				System.out.println("Starting Deal Number "
						+ Integer.toString(dealList.size() + 1));
			}
			deal();
			bid();
			redeal();
			layAll();
			dealList.add(deal);
			deal = new Deal();
			deal.dealer = nextID(dealer);
		}
		if (verbose && this.moreThanSixInt != 0) {
			System.out.println(((float) this.moreThanSixInt) / dealList.size());
		}
		if (dealList.size() < DEALS_BEFORE_ENDING) {
			// System.out.println("GAME ENDED at " + deals.size() + " Deals");
		}
		if (evenScoreInt > oddScoreInt) {
			// System.out.println("EVEN ACTUALLY WON");
			return Game.EVEN;
		} else {
			// System.out.println("ODD ACTUALLY WON");
			return Game.ODD;
		}
	}

	public void setupBidFunc(BidProfile even, BidProfile odd) {
		evenBidProfile = even;
		oddBidProfile = odd;
		bidFunc = true;
	}

	public void deal() {
		deal.reset();
		Stack hand;
		Card card;
		boolean misdeal = true;
		int nextID = deal.dealer;
		deck.addPitchDeck();
		deck.shuffle();
		do {
			nextID = nextID(nextID);
			hand = playersList.get(nextID).hand;
			hand.clear();
			for (int j = 0; j < CARDS_DEALT; j++) {
				card = deck.popCard();
				if (card.rank < 4|| card.rank > 9) misdeal = false;
				hand.addCard(card);
			}
			if (misdeal) {
				deal();
				return;
			}
			hand.sortForBid();
		} while (deal.dealer != nextID);
		current_player_index = nextID(deal.dealer);
		current_action = "bid";
	}

	public Bid computerBid() {
		CalcBid cb = new CalcBid(CalcBid.SINGLE_THREAD);
		Integer highBid = 0;
		int suit = 0;
		HashMap<String, HashMap<Integer, HashMap<String, BigDecimal>>> res = cb.forAllBidsAndSuits(deal.dealer,playersList.get(current_player_index).hand, current_player_index);
		for (Map.Entry<String, HashMap<Integer, HashMap<String, BigDecimal>>> entryBySuit : res.entrySet()) {
			HashMap<Integer, HashMap<String, BigDecimal>> bids = entryBySuit.getValue();
			for (Map.Entry<Integer, HashMap<String, BigDecimal>> entryByBid : bids.entrySet()) {
				if (entryByBid.getKey() > highBid && entryByBid.getValue().get("averagePointsGained").doubleValue() > 5.0) {
					highBid = entryByBid.getKey();
					suit = Suit.fromHumanReadable(entryBySuit.getKey()).id();
				}
			}
		}
		return new Bid(current_player_index,highBid,suit);
	}

	public void processBid(Bid bid) {
		deal.bids.add(bid);
		events.add("bid,"+current_player_index);
		if (deal.highBid.bid < bid.bid) { 
			deal.highBid = bid; 
		} else { //if (deal.highBid.bid >= bid.bid) {
			deal.bids.get(deal.bids.size()-1).bid = Bid.PASS;
		}
		if (current_player_index == deal.dealer) {
			if (deal.highBid.bid < Game.LOW_BID) {
				bid.bid = Game.FORCE_BID;
				deal.highBid = bid;
				current_player_index = deal.dealer;
				current_action = "lay";
			}
			if (deal.highBid.suit == 0) {
				current_action = "bidsuit";
				current_player_index = deal.highBid.playerID;
			} else {
				redealForSuit(); // this takes care of current_..
			}
		} else {
			// SAME //current_action = "bid"
			current_player_index = nextID(current_player_index);
		}
	}

	public void processLay(Lay currentLay) {
		Trick trick = deal.topTrick();

		if (currentLay.layCard.type != Stack.TRUMP && playersList.get(current_player_index).hand.out) {
			// they're out so don't actually lay this, lay a none card and don't fire an event for it
			currentLay.layCard = new Card();
		} else {
			events.add("lay,"+current_player_index+","+currentLay.layCard.toLetters());
			fromOutstandingAndOtherPeoplesCardsRemove(currentLay);
		}
		trick.add(currentLay);

		// update hand.out .. do it after actually laying it so players will know that the person is out now
		if (currentLay.layCard.type != Stack.TRUMP && trick.trickLaysList.get(0).layCard.type == Stack.TRUMP) {
			playersList.get(current_player_index).hand.out = true;
		}

		// TRICK IS OVER
		if (trick.trickLaysList.size() == nPlayers) {
			events.add("clearlays");
			assignDealPointsToHighestLayFor(trick);

			// highest layer gets to start next trick
			current_player_index = trick.highestLay.playerID;

   		// DEAL IS OVER
			if (playersList.get(current_player_index).out()) {
 		      assignGamePointsForDeal();

 		      // GAME IS OVER
 		      if (dealList.size() >= DEALS_BEFORE_ENDING || (evenScoreInt >= WINNING_SCORE || oddScoreInt >= WINNING_SCORE && evenScoreInt != oddScoreInt)) {
			   	// don't go immediately to next game b/c then people won't be able to see the results
			   	dealList.add(deal); // archive last
			   	current_action = "newGameRequest";
			   	// if (evenScoreInt > oddScoreInt) {
			   	// 	return Game.EVEN;
			   	// } else {
			   	// 	return Game.ODD;
			   	// }
			   } else {
 			//    if (((evenScoreInt <= 31 && oddScoreInt <= 31) || evenScoreInt == oddScoreInt)
				// && dealList.size() < DEALS_BEFORE_ENDING) {
			   	current_action = "deal";
			   	current_player_index = nextID(deal.dealer);

			   	dealList.add(deal); // archive last
			   	deal = new Deal();
			   	deal.dealer = current_player_index;
			   	deal();
			   	events.add("clearlays");
			   }
			} else {
				deal.tricks.add(new Trick());
			} // endif DEAL IS OVER
		} else {
			current_player_index = nextID(current_player_index);
		} // endif TRICK IS OVER
	}

	public void fromOutstandingAndOtherPeoplesCardsRemove(Lay lay) {
		Player player;
		int playersSize = playersList.size();
		if (lay.layCard.type == Stack.TRUMP) {
			deal.outstanding.removeRS(lay.layCard.rank, lay.layCard.suit);
  			// other people's cards
			for (int j = 0; j < playersSize; j++) {
				player = playersList.get(j);
				if (lay.playerID != player.ID) {
					if (lay.layCard.rank == 3) { player.three_unlaid_and_not_mine = false; }
					player.other_peoples_cards.removeRS(lay.layCard.rank, lay.layCard.suit);
				}
			} // for Players
		}
	}


	public void assignGamePointsForDeal() {
		if (team(deal.highBid.playerID) == Game.EVEN) { // even bid
			if (deal.highBid.bid == 10) { // shot moon
				if (deal.even_score_change == 10) { // won moon shot
					oddScoreInt += deal.odd_score_change;
					evenScoreInt = Math.max(WINNING_SCORE, oddScoreInt+1);
				} else { // lossed moon shot
					oddScoreInt += deal.odd_score_change;
					oddScoreInt = Math.max(WINNING_SCORE, oddScoreInt);
					evenScoreInt = -1 * WINNING_SCORE;
				}
			} else {
			   if (deal.highBid.bid > deal.even_score_change) { // went set
				   deal.even_score_change = -1 * deal.highBid.bid;
			   }
				evenScoreInt += deal.even_score_change;
				oddScoreInt += deal.odd_score_change;
			}
		} else {// ODD was high bid
			if (deal.highBid.bid == 10) { // shot moon
				if (deal.odd_score_change == 10) { // won moon shot
					evenScoreInt += deal.even_score_change;
					oddScoreInt = Math.max(WINNING_SCORE, evenScoreInt+1);
				} else { // lossed moon shot
					evenScoreInt += deal.even_score_change;
					evenScoreInt = Math.max(WINNING_SCORE, evenScoreInt);
					oddScoreInt = -1 * WINNING_SCORE;
				}
			} else {
			   if (deal.highBid.bid > deal.odd_score_change) { // went set
				   deal.odd_score_change = -1 * deal.highBid.bid;
			   }
				oddScoreInt += deal.odd_score_change;
				evenScoreInt += deal.even_score_change;
			}
		}
	}

	public void assignDealPointsToHighestLayFor(Trick trick) {
		if (team(trick.highestLay.playerID) == Game.EVEN) {
			deal.even_score_change += trick.catchablePoints;
		} else {
			deal.odd_score_change += trick.catchablePoints;
		}
		if (trick.deuceLayer == Player.NONE) {
		} else if (team(trick.deuceLayer) == Game.EVEN) {
			deal.even_score_change += 1;
		} else if (team(trick.deuceLayer) == Game.ODD) {
			deal.odd_score_change += 1;
		}
	}

	public Lay computerLay() {
		return playersList.get(current_player_index).layFor(this);
	}


	public void setupForLaying () {
		current_player_index = deal.highBid.playerID;
		// setup other_people's Cards for everyone
		Player player;
		int outstandingSize = deal.outstanding.size();
		Card card;
		for (int j = 0; j < playersList.size(); j++) {
			player = playersList.get(j);
			player.three_unlaid_and_not_mine = false;
			player.other_peoples_cards.clear();
			for (int i = 0; i < outstandingSize; i++) {
				card = deal.outstanding.getCard(i);
				if (!player.hand.hasCard(card)) {
					player.other_peoples_cards.addCard(card);
					if (card.rank== 3 && card.type == Stack.TRUMP) {
						player.three_unlaid_and_not_mine = true;
					}
				}
			}
		} // for players setting up other_peoples_cards

	}

	public void redealForSuit() {
		deal.changeSuitOfOutstandingTo(deal.highBid.suit);
		Stack playersHand;
		int nextID = deal.dealer;
		do {
			nextID = nextID(nextID);
			if (deal.highBid.playerID != nextID) { // skip bidder
				playersHand = playersList.get(nextID).hand;
				playersHand.pitch(deal.highBid.suit, true); // to trumpify and drop non-trump
				while (playersHand.size() < 6) {	playersHand.addCard(deck.popCard()); }
				playersHand.pitch(deal.highBid.suit, false); // to trumpify
				// burnToSix sorts hands to eliminate sorting during lay_for and lay_card
				playersHand.sortLowToHigh(); //doesn't trumpify
				// playersHand.burnToSix(deal.highBid.suit, burntByPlayer);
				// deal.outstanding.removeCards(burntByPlayer);
				// burnt.transfer(burntByPlayer);
			}
		} while (deal.dealer != nextID);
		playersHand = playersList.get(deal.highBid.playerID).hand;
		playersHand.transfer(deck);
		playersHand.pitch(deal.highBid.suit, false);
		// TODO ask human player which point to burn
		// TODO tell everyone what was burnt
		playersHand.burnToSix(deal.highBid.suit, burntByPlayer);
		deal.outstanding.removeCards(burntByPlayer);
		burnt.transfer(burntByPlayer);
		current_player_index = deal.highBid.playerID;
		current_action = "lay";
	}

/*
	while (((evenScoreInt <= 31 && oddScoreInt <= 31) || evenScoreInt == oddScoreInt)
				&& dealList.size() < DEALS_BEFORE_ENDING) {
			if (verbose) {
				System.out.println("Starting Deal Number "
						+ Integer.toString(dealList.size() + 1));
			}
			deal();
			bid();
			redeal();
			layAll();
			dealList.add(deal);
			deal = new Deal();
			deal.dealer = nextID(dealer);
*/

	public void bid() {
		int thisID = deal.dealer;
		Bid highBid = new Bid(-1, 4, Stack.JOKERS);
		Bid lastBid;
		do {
			thisID = nextID(thisID);
			if (this.bidFunc) {
				if (team(thisID) == Game.EVEN) {
					deal.bids.add(this.evenBidProfile.bid(playersList.get(thisID),
							this));
				} else {
					deal.bids.add(this.oddBidProfile.bid(playersList.get(thisID),
							this));
				}
			} else {
				Population pop = new Population(1, 1, 1, 1); // just load up
																// alpha
				BidProfile testBP = pop.getAlpha();
				deal.bids.add(testBP.bid(playersList.get(thisID), this));
				// players.get(thisID).bidTest(this);
			}

			lastBid = deal.bids.get(deal.bids.size() - 1);
			if (verbose) {
				playersList.get(thisID).soplHand();
				System.out.println(Integer.toString(thisID) + " bid "
						+ Integer.toString(lastBid.bid) + " in " + Stack.suitToString(lastBid.suit));
			}
			if (lastBid.bid > highBid.bid) {	highBid = lastBid; }
			if (highBid.bid < Game.LOW_BID && deal.dealer == thisID) { // force bid
				lastBid.bid = Game.FORCE_BID;
				highBid = lastBid;
			}
		} while (deal.dealer != thisID);
		deal.highBid = highBid;
		if (verbose) {
			System.out.println(Integer.toString(deal.highBid.playerID) +
					" got bid in " + Stack.suitToString(deal.highBid.suit) +
					" with " + Integer.toString(deal.highBid.bid));
		}
	}

	public void redeal() {
		deal.changeSuitOfOutstandingTo(deal.highBid.suit);
		Stack playersHand;
		int nextID = deal.dealer;
		do {
			nextID = nextID(nextID);
			if (deal.highBid.playerID != nextID) { // skip bidder
				playersHand = playersList.get(nextID).hand;
				// players.get(nextID).soplHand();
				playersHand.pitch(deal.highBid.suit, true);
				while (playersHand.size() < 6) {
					playersHand.addCard(deck.popCard());
				}
				playersHand.pitch(deal.highBid.suit, false);
				playersHand.burnToSix(deal.highBid.suit, burntByPlayer);
				deal.outstanding.removeCards(burntByPlayer);
				// sort hands to eliminate sorting during lay_for and lay_card
				// happens in burntTosix playersHand.sort();
				if (verbose) {
					playersList.get(nextID).soplHand();
					if (burntByPlayer.size() != 0) {
						System.out.println("~~~~~~~" + nextID
								+ "Had to burn these");
						burntByPlayer.sopl();
						System.out.println("~~~~~~~");
					}
				}
				burnt.transfer(burntByPlayer);
			}

		} while (deal.dealer != nextID);
		playersHand = playersList.get(deal.highBid.playerID).hand;
		playersHand.transfer(deck);
		playersHand.pitch(deal.highBid.suit, false); // need to do this to
														// trumpify
		playersHand.burnToSix(deal.highBid.suit, burntByPlayer);
		deal.outstanding.removeCards(burntByPlayer);
		if (verbose) {
			playersList.get(deal.highBid.playerID).soplHand();
			if (burntByPlayer.size() != 0) {
				System.out.println("~~~~~~~" + deal.highBid.playerID
						+ "Had to burn these");
				burntByPlayer.sopl();
				System.out.println("~~~~~~~");
			}
		}
		burnt.transfer(burntByPlayer);
	}

	public void layAll() {
		int nextID = deal.highBid.playerID;
		if (verbose) {
			System.out
					.println("============== Begin laying with bidder ================");
		}
		// setup other_people's Cards for everyone
		Player player;
		int outstandingSize = deal.outstanding.size();
		Card card;
		for (int j = 0; j < playersList.size(); j++) {
			player = playersList.get(j);
			player.three_unlaid_and_not_mine = false;
			player.other_peoples_cards.clear();
			for (int i = 0; i < outstandingSize; i++) {
				card = deal.outstanding.getCard(i);
				if (!player.hand.hasCard(card)) {
					player.other_peoples_cards.addCard(card);
					if (card.rank== 3 && card.type == Stack.TRUMP) {
						player.three_unlaid_and_not_mine = true;
					}
				}
			}
		}
		// end of other_people's Cards setup
		do {
			nextID = layTrick(nextID);
		} while (nextID != Player.NONE);
		// game is over so did bidder get bid?
		if (team(deal.highBid.playerID) == Game.EVEN) {
			if (deal.highBid.bid > deal.even_score_change) { // went set
				deal.even_score_change = -1 * deal.highBid.bid;
			}
		} else { // ODD
			if (deal.highBid.bid > deal.odd_score_change) { // went set
				deal.odd_score_change = -1 * deal.highBid.bid;
			}
		}
		evenScoreInt += deal.even_score_change;
		oddScoreInt += deal.odd_score_change;
		if (verbose) {
			System.out.println("Even + "
					+ Integer.toString(deal.even_score_change) + ", Odd + "
					+ Integer.toString(deal.odd_score_change));
			System.out.println("Even Score =" + Integer.toString(evenScoreInt)
					+ ", Odd Score = " + Integer.toString(oddScoreInt));
		}
	}

	public int layTrick(int leaderID) {
		int points = 0;
		int deuce_layer = Player.NONE;
		Lay highest_lay = new Lay(Player.NONE, new Card(), "NONE");
		Lay currentLay;
		int nextID = leaderID;
		do {
			// card, tag
			if (playersList.get(nextID).out()) {
				return Player.NONE;
			}
			int sizeBefore = playersList.get(nextID).hand.size();
			currentLay = playersList.get(nextID).layFor(this);
			int sizeAfter =  playersList.get(nextID).hand.size();
			if (sizeBefore != sizeAfter + 1) {
				System.out.println("I'll be damned");
			}
			deal.topTrick().trickLaysList.add(currentLay);
			if (currentLay.layCard.rank == 2
					&& currentLay.layCard.type == Stack.TRUMP) {
				deuce_layer = nextID;
			} else {
				points += currentLay.layCard.points;
			}
			if (verbose) {
				System.out.println(Integer.toString(currentLay.playerID)
						+ " laid " + currentLay.layCard.toString() + " because "
						+ currentLay.reason);
			}
			if (highest_lay.layCard.type == Stack.NO_TRUMP) {
				highest_lay = currentLay;
			} // first lay is always the highest
			else if (highest_lay.layCard.rank < currentLay.layCard.rank
					&& currentLay.layCard.type == Stack.TRUMP) {
				highest_lay = currentLay;
			}
			nextID = nextID(nextID);
		} while (nextID != leaderID);
		// remove lays from outstanding
		Lay lay;
		int trickLaysListSize = deal.tricks.get(deal.tricks.size() - 1).trickLaysList.size();
		Trick lastTrick = deal.tricks.get(deal.tricks.size() - 1);
		Player player;
		int playersSize = playersList.size();
		for (int i = 0; i < trickLaysListSize; i++) {
			lay = lastTrick.trickLaysList.get(i);
			if (lay.layCard.type == Stack.TRUMP) {
				//deal.outstanding.removeCard(new Card(lay.layCard.rank, lay.layCard.suit));
				deal.outstanding.removeRS(lay.layCard.rank, lay.layCard.suit);
				// other people's cards
				for (int j = 0; j < playersSize; j++) {
					player = playersList.get(j);
					if (lay.playerID != player.ID) {
						if (lay.layCard.rank == 3) { player.three_unlaid_and_not_mine = false; }
						//player.other_peoples_cards.removeCard(new Card(lay.layCard.rank, lay.layCard.suit));
						player.other_peoples_cards.removeRS(lay.layCard.rank, lay.layCard.suit);
					}
				} // for
				// end of update other people's cards
			}
		}
		// give highest_layer the points
		if (verbose) {
			System.out.println(Integer.toString(highest_lay.playerID)
					+ " won trick capturing " + points + " points "
					+ highest_lay.layCard.toString());
		}
		if (team(highest_lay.playerID) == Game.EVEN) {
			deal.even_score_change += points;
		} else {
			deal.odd_score_change += points;
		}
		if (deuce_layer == Player.NONE) {
		} else if (team(deuce_layer) == Game.EVEN) {
			deal.even_score_change += 1;
			if (verbose) {
				System.out.println("Deuce brought home by "
						+ Integer.toString(deuce_layer));
			}
		} else if (team(deuce_layer) == Game.ODD) {
			deal.odd_score_change += 1;
			if (verbose) {
				System.out.println("Deuce brought home by "
						+ Integer.toString(deuce_layer));
			}
		}
		// if self.verbose: print("Even Points =",
		// self.games[-1]['deals'][-1]['even_score_change'], ", Odd Points = ",
		// self.games[-1]['deals'][-1]['odd_score_change'])
		deal.tricks.add(new Trick());
		return highest_lay.playerID;
	}

	public void doTwoPeopleHaveSameCard() {
		Stack one, two;
		for (int i = 0; i < playersList.size(); i += 1) {
			one = playersList.get(i).hand;
			for (int j = i+1; j < playersList.size(); j += 1) {
				two = playersList.get(j).hand;
				for (int k = 0; k< one.size(); k += 1) {
					if (two.hasCard(one.getCard(k))) {
						k = 5;
					}
				}

			}
		}
	}

	public List<String> getPlayersNames() {
		List<String> res = new ArrayList<String>();
		for (int i = 0; i < nPlayers; i++) {
			res.add(playersList.get(i).handle);
		}
		return res;
	}

}
