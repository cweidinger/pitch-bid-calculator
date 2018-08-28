package pitch.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Fitness {
	public BidProfile even;
	public BidProfile odd;

	public Fitness() {
	}

	public Fitness(BidProfile even, BidProfile odd) {
		this.even = even;
		this.odd = odd;
	}

	public float calculateEvenFitnessFor(int totalGames) { // throws
															// InterruptedException,
															// /*java.util.concurrent.*/ExecutionException
		try {
			int NCPU = Runtime.getRuntime().availableProcessors();
			ExecutorService executor = Executors.newFixedThreadPool(NCPU);
			List<Future<Long>> results = new ArrayList<Future<Long>>();
			for (int i = 0; i < NCPU; i++) {
				results.add(executor.submit(new GamesEvenWon(totalGames / NCPU,
						even, odd)));
			}
			executor.shutdown();
			int totalEvenGamesWon = 0;
			for (Future<Long> result : results) {
				totalEvenGamesWon += result.get();
			}
			return ((float) totalEvenGamesWon) / totalGames;
		} catch (Exception e) {
			System.err.println("Error on Fitness.calcEvenFitness(): "
					+ e.getMessage());
			return 0.0f;
		}
	}
}

class GamesEvenWon implements Callable<Long> {
	private final int numberOfGames;
	public BidProfile even;
	public BidProfile odd;
	public Game game;

	public GamesEvenWon(int numberOfGames, BidProfile even, BidProfile odd) {
		this.numberOfGames = numberOfGames;
		this.even = even;
		this.odd = odd;
	}

	@Override
	public Long call() {
		Long evenWins = new Long(0);
		game = new Game();
		game.verbose = false;
		game.setupBidFunc(even, odd);
		for (int i = 0; i < 4; i++) {
			game.playersList.add(new Player(Integer.toString(i), i, true,
					game.verbose));
		}
		for (int i = 0; i < this.numberOfGames; i++) {
			int result = 0;
			result = game.play();
			if (Game.EVEN == result) {
				evenWins++;
			}
			game.reset();
		}
		return evenWins;
	}
}
