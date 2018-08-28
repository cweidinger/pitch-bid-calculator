package pitch.simulation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Population {
	public List<BidProfile> bidProfiles;
	public BidProfile alpha;
	public boolean sorted;

	public int maxSize;
	public int pop_limit;
	public int generations;
	public int fecundity;
	public int budity;
	public int num_games;
	public boolean verbose;

	public Population(int pop_limit, int budity, int fecundity, int num_games) {
		this.verbose = true;
		this.pop_limit = pop_limit;
		this.budity = budity;
		this.fecundity = fecundity;
		this.num_games = num_games;
		this.maxSize = pop_limit * (1 + fecundity + budity);
		bidProfiles = new ArrayList<BidProfile>(maxSize);
		if (!load()) {
			BidProfile soleParent = new BidProfile();
			soleParent.setupDefaults();
			bidProfiles.add(soleParent);
		}
		alpha = getAlpha();
	}

	public void vary(int budity, int fecundity) {
		// crossover from breeding
		List<BidProfile> children = new ArrayList<BidProfile>(maxSize);
		List<BidProfile> buds = new ArrayList<BidProfile>(maxSize);
		// LATER List mutantChildren = new ArrayList<BidProfile>(maxSize);
		alpha = getAlpha();
		for (int i = 0; i < bidProfiles.size(); i++) {
			for (int b = 0; b < budity; b++) {
				buds.add(bidProfiles.get(i).bud());
			}
			if (i != 0) {
				for (int c = 0; c < fecundity; c++) {
					children.add(alpha.mate(bidProfiles.get(i)));
				}
			}
		}
		// if (verbose)
		// {System.out.println(Arrays.toString(alpha.bp.entrySet().toArray()));}
		if (verbose) {
			printBidFromCardString("as,ks,qs,js,jc,4d,5d,6d,7d", 9);
			printBidFromCardString("as,ks,qs,js,2d,4d,5d,6d,7d", 8);
			printBidFromCardString("as,ks,qs,2s,2d,4d,5d,6d,7d", 7);
			printBidFromCardString("as,ks,qs,2d,3d,4d,5d,6d,7d", 6);
		}
		if (verbose) {
			System.out.println("\t Old:" + Integer.toString(bidProfiles.size())
					+ "\t Children: " + children.size() + "\t Buds:"
					+ buds.size());
		}
		bidProfiles.addAll(children);
		bidProfiles.addAll(buds);
		sorted = false;
	}

	public void printBidFromCardString(String cardsString, int expectedBid) {
		int dealer = 0;
		int player = 2;
		Stack hand = new CardsStack(cardsString);
		Bid b = alpha.getBidFor(dealer, player, hand);
		hand.sopl(b.bid + " in suit " + b.suit + " expected " + expectedBid);
	}

	public void cull_all_against_alpha(int numberOfGames) {
		// recalculate their fitnesses
		sorted = false;
		alpha = getAlpha();
		Fitness f = new Fitness();
		f.odd = alpha;
		for (int i = 0; i < bidProfiles.size(); i++) {
			f.even = bidProfiles.get(i);
			float fit = f.calculateEvenFitnessFor(numberOfGames);
			f.even.setFitness(fit);
		}
		if (bidProfiles.size() > 1) { // face off alpha with #2
			f.even = alpha;
			f.odd = bidProfiles.get(1);
			float fit = f.calculateEvenFitnessFor(numberOfGames);
			alpha.setFitness(fit);
		}
		// sort
		sortHighToLow(true);
		// drop all others
		while (bidProfiles.size() > pop_limit) {
			bidProfiles.remove(pop_limit);
		}
	}

	public BidProfile getAlpha() {
		if (!sorted) {
			sortHighToLow(true);
		}
		return bidProfiles.get(0);
	}

	public void sortHighToLow(boolean highToLow) { // Low to High
		if (highToLow) {
			Collections.sort(bidProfiles, new Comparator<BidProfile>() {
				public int compare(BidProfile a, BidProfile b) {
					float aFit = a.getFitness();
					float bFit = b.getFitness();
					return (aFit < bFit) ? 1 : (aFit > bFit) ? -1 : 0;
				}
			});
		} else { // lowToHigh
			Collections.sort(bidProfiles, new Comparator<BidProfile>() {
				public int compare(BidProfile a, BidProfile b) {
					float aFit = a.getFitness();
					float bFit = b.getFitness();
					return (aFit < bFit) ? -1 : (aFit > bFit) ? 1 : 0;
				}
			});
		}
		sorted = true;
	}

	public void evolve(int generations) {
		for (int i = 0; i < generations; i++) {
			System.out.println("================ Generation " + (i + 1)
					+ " of " + generations + " =============");
			vary(budity, fecundity);
			cull_all_against_alpha(num_games);
			save();
		}
	}

	public void printStats() {
		int buds = 0;
		int children = 0;
		for (int i = 0; i < bidProfiles.size(); i++) {
			if (Math.abs(bidProfiles.get(i).bp.get(BidProfile.ORIGIN)
					- BidProfile.BUD) < 0.1f) {
				buds += 1;
			} else {
				children += 1;
			}
		}
		System.out.println("Population Breakdown after culling: "
				+ bidProfiles.size() + " Profiles, " + buds + " Buds, "
				+ children + " Children");
	}

	public void save() {
		try {
			FileOutputStream fos = new FileOutputStream("pop.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeInt(bidProfiles.size());
			for (int i = 0; i < bidProfiles.size(); i++) {
				oos.writeObject(bidProfiles.get(i).bp);
			}
			oos.close();
		} catch (Exception e) {
			System.err.println("error on Population.save(): " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public boolean load() {
		try {
			FileInputStream fis = new FileInputStream("pop.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			int nBidProfiles = ois.readInt();
			if (maxSize < nBidProfiles) {
				bidProfiles = new ArrayList<BidProfile>(nBidProfiles);
				maxSize = nBidProfiles;
			}
			float last = 1.0f;
			for (int i = 0; i < nBidProfiles; i++) {
				BidProfile bidProfile = new BidProfile();
				bidProfile.bp = (Map<Integer, Float>) ois.readObject();
				bidProfiles.add(bidProfile);
				if (bidProfile.bp.get(BidProfile.FITNESS) > last) {
					System.err
							.println("fitnesses should be sorted monotonically descending");
				}
				last = bidProfile.bp.get(BidProfile.FITNESS);
			}
			ois.close();
			return true;
		} catch (Exception e) {
			System.err.println("error on Population.save(): " + e.getMessage());
			return false;
		}
	}
}
