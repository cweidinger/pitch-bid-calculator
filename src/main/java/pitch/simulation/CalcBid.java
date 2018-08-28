/*
  use generic algorithm's result
  //match situation to BidDict and return best guess
  do specific research
 * calculate odds of going set at any given bid so I can match that against my odds of winning at any specific bid - and store in bp.ser data
 * allow to enter new information quickly, if they bid 6 then no use thinking about anything less than 7
 */

package pitch.simulation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CalcBid {

	public static final int NUMBER_OF_GAMES = 100
	;
	public static final boolean TEST_DEAL = false;

	public static final int MULTI_THREAD = 1;
	public static final int SINGLE_THREAD = 2;
	public int thread;

	/*
	 * JavaDoc stuff in here
	 */
	public CalcBid(int thread) {
		this.thread = thread;
		// body
	}

	/*
	 * play game with hand at bid of 5,6,7,8,9,10 a heck of a lot of times and
	 * print percentages let it run for 10s win percentages how are the other
	 * people going to bid? 1. assume they don't bid? - human will have to make
	 * tradeoff 2. assume they bid as the alpha BidProfile does? - bidprofile
	 * will have to be good then just go for highest percentage
	 */
	public HashMap<String, HashMap<Integer, HashMap<String, BigDecimal>>> forAllBidsAndSuits(
            int dealer, Stack hand, int bidder) {
		HashMap<String, HashMap<Integer, HashMap<String, BigDecimal>>> ret = new HashMap<String, HashMap<Integer, HashMap<String, BigDecimal>>>(
				20);
		// return ret;
		for (Suit suit : Suit.four) {
			HashMap<Integer, HashMap<String, BigDecimal>> suitMap = new HashMap<Integer, HashMap<String, BigDecimal>>(
					6);
			if (TEST_DEAL) {
				suit = Suit.DIAMONDS;
			}
			// System.out.println(Card.suitToString(suit));
			for (int bid = 5; bid <= 10; bid++) {
				HashMap<String, BigDecimal> res = printWinPercentageAtBid(
						dealer, hand, new Bid(bidder, bid, suit.id()));
				if (res.get("averagePointsGained").intValue() < 4) {
					break; // don't calculate higher bids if a lower bid wasn't
							// even noteworthy
				} else {
					// res.put("suit", suit);
					suitMap.put(bid, res);
				}
			}
			ret.put(suit.humanReadable(), suitMap);
		}
		return ret;
	}

	public String percent(int top, int bottom) {
		return Integer.toString((100 * top) / bottom) + "%";
	}

	public HashMap<String, BigDecimal> printWinPercentageAtBid(int dealer,
                                                               Stack hand, Bid bid) {
		HashMap<String, BigDecimal> res = new HashMap<String, BigDecimal>(2);
		int wonDeals = 0;
		int pointsGained = 0;
		int pointsGainedThisDeal = 0;

		// setup game
		Game game = new Game();
		game.verbose = TEST_DEAL;

		if (thread == SINGLE_THREAD || NUMBER_OF_GAMES < 80) {
			for (int i = 0; i < NUMBER_OF_GAMES; i++) {
				pointsGainedThisDeal = bidderPointsAtBid(game, dealer, hand, bid);
				if (TEST_DEAL) {
					System.exit(0);
				}
				if (pointsGainedThisDeal > 0) {
					wonDeals += 1;
				}
				pointsGained += pointsGainedThisDeal;
			}
		} else if (thread == MULTI_THREAD) {
			List<Integer> pointsGainedEachDeal = IntStream.range(0, NUMBER_OF_GAMES)
					.parallel()
					.map(n -> bidderPointsAtBid(new Game(), dealer, hand, bid))
					.boxed()
					.collect(Collectors.toList());
			wonDeals += (int)pointsGainedEachDeal.stream()
					.filter(points -> points > 0)
					.count();
			pointsGained += pointsGainedEachDeal.stream()
					.mapToInt(Integer::intValue)
					.sum();


			// This is how I did multithreading in Java7 :(
//			int NCPU = Runtime.getRuntime().availableProcessors();
//			ExecutorService executor = Executors.newFixedThreadPool(NCPU);
//			List<Future<int[]>> results = new ArrayList<Future<int[]>>();
//			for (int i = 0; i < NCPU; i++) {
//				game = new Game();
//				game.verbose = TEST_DEAL;
//				for (int j = 0; j < 4; j++) {
//					game.playersList.add(new Player(Integer.toString(j), j, true,
//							game.verbose));
//				}
//				results.add(executor.submit(new BidderTotalScore((int) NUMBER_OF_GAMES / NCPU, game, dealer, hand, bid)));
//			}
//			executor.shutdown();
//			int [] mpRes = {1,2};
//			for (Future<int[]> result : results) {
//				try {
//					mpRes = result.get();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} catch (ExecutionException e) {
//					e.printStackTrace();
//				}
//				wonDeals += mpRes[0];
//				pointsGained += mpRes[1];
//			}
		}
		
		// pointsGained, wonDeals ... soFarDeals should be equal to NUMBER_OF_GAMES
		float averagePointsGained = ((float) pointsGained) / NUMBER_OF_GAMES;
		res.put("averagePointsGained",
				new BigDecimal(Float.toString(averagePointsGained)));
		res.put("percentWon",
				new BigDecimal(Integer.toString((100 * wonDeals) / NUMBER_OF_GAMES)));
		return res;
		// if (averagePointsGained >=4) {
		// System.out.println("At a bid of " + bid.bid + ", bidder won " +
		// wonDeals + " / " + soFarDeals + " = " + percent(wonDeals, soFarDeals)
		// +
		// " averaging " + averagePointsGained + " points per deal.");
		// }
	}

	public int bidderPointsAtBid(Game game, int dealer, Stack hand, Bid bid) {
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
			hand.sopl();
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
				game.playersList.get(nextID).hand.clear();
				 // the previous hand should have cleared this up??????????
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
