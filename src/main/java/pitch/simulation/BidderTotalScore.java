package pitch.simulation;

import java.util.concurrent.Callable;

class BidderTotalScore implements Callable<int[]> {
	private int numberOfGames;
	public Game game;
	private int dealer;
	private Stack hand;
	private Bid bid;

	public BidderTotalScore(int numberOfGames, Game game, int dealer, Stack hand, Bid bid) {
		this.numberOfGames = numberOfGames;
		this.game = game;
		this.dealer = dealer;
		this.hand = hand;
		this.bid = bid;

	}

	@Override
	public int [] call() {
		int wonDeals = 0;
		int pointsGained = 0;
		int pointsGainedThisDeal;
		for (int i = 0; i < this.numberOfGames; i++) {
			pointsGainedThisDeal = bidderPointsAtBid();
			//if (TEST_DEAL) { System.exit(0); }
			if (pointsGainedThisDeal > 0) {
				wonDeals += 1;
			}
			pointsGained += pointsGainedThisDeal;
		}
		return new int [] { wonDeals, pointsGained };
	}
	
	public int bidderPointsAtBid() {
		game.reset();
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
			game.playersList.get(bid.playerID).addCard(card.getCopy());
			game.deck.removeCard(card);
		}
		// #######
		do {
			nextID = game.nextID(nextID);
			if (nextID != bid.playerID) {
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
		// game.highBid = new Bid(bid, bid.playerID, suit);
		game.deal.highBid = bid;
		// END bid();
		game.redeal();
		game.layAll();
		// tidy up and report
		int bidderScore = (game.team(bid.playerID) == Game.EVEN) ? game.evenScoreInt
				: game.oddScoreInt;
		return bidderScore;
	}
	

}


