package com.dprogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Generally represents a knight move on the keypad.
 * 
 * Implements the algorithm for computing the number of all sequences
 * of keys of <code>n</code> length, with a restriction of at most 2 vowels
 * in a sequence.
 * 
 * <br>This implementation uses memorisation strategy to speed up the computation.
 * This means that each computed result is stored in a cache for reuse. This 
 * reduces a potential exponential time complexity to linear time O(n). 
 *  
 * @author godwin
 *
 */
public class KnightMove extends RecursiveTask<Long>{

	private static final long serialVersionUID = -5365516611882162033L;

	private final int n;
	private final Key key;
	private final ConcurrentMap<String, Long> cache;
	private final int vowelCount;
	
	/**
	 * Constructs an instance of this class.
	 * 
	 * @param n the length of each key sequence. This is decremented in the course of computation.
	 *  zero (0) or negative value of <code>n</code> will produce a computation of <code>1</code>
	 * @param key this is a physical key on the Keypad, which represents a coordinate or position
	 *  on the Key from which this level of computation begins.
	 * @param cache Used in storing the computed results for reuse. This is transparent cache shared 
	 *  by all instances of this class.
	 * @param vowelCount Keeps track of the number of vowels remaining in a sequence. <code>vowelCount<=0</code>
	 *  means: no more vowels is allowed in a sequence.
	 */
	public KnightMove(final int n, final Key key, final ConcurrentMap<String, Long> cache, final int vowelCount) {
		this.n = n;
		this.key = key;
		this.cache = cache;
		this.vowelCount = vowelCount;
	}

	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Requires an argument like: java KnightMove 10");
			System.exit(0);
		}
		
		int n = Integer.parseInt(args[0]);
		System.out.println("#sequences = " + computeNumberOfSequencesFor(n));
	}
	
	/**
	 * Computes the number of sequences for the given <code>n</code>.
	 * 
	 * <p> By default the configuration for the cache especially is for n=10.
	 * It might require some tweaking for n=16 (maybe: new ConcurrentHashMap<>(1200000000,1f,100);) and n=32.
	 * 
	 * <p>This is just a rough guide, proper configuration will require measurements.  
	 */
	public static long computeNumberOfSequencesFor(int n) {
		ConcurrentMap<String, Long> cache = new ConcurrentHashMap<>(1100000,1f,100);
		KnightMove knightMove = new KnightMove(n, null, cache, 2);
		ForkJoinPool pool = new ForkJoinPool();		
		Long result = pool.invoke(knightMove);
		return result;
	}
	

	@Override
	protected Long compute() {
		
		if (n <= 0) { /* Base case */
			return Long.valueOf(1L);
		} 
			
		String computeLevel = (key == null) ? new StringBuilder(5).append("null").append(n).toString() : 
			new StringBuilder(6).append(key.name()).append(n).append(vowelCount).toString();
		
		/* Check the cache for existing computation */
		if (cache.containsKey(computeLevel)) {
			return cache.get(computeLevel);
		}
		
		/* Compute further sequences and aggregate results */
		long sum = sumOf(validKeySequences());
		
		/* Stores the computed result for re-use */
		return cacheComputedKeySequencesFor(computeLevel, sum);
	}


	private Long cacheComputedKeySequencesFor(String cacheKey, long sum) {
		Long result = Long.valueOf(sum);
		cache.put(cacheKey, result);
		return result;
	}


	private long sumOf(List<KnightMove> knightMoveTasks) {
		long sum = 0;
		for (KnightMove task : knightMoveTasks) {
			sum += task.join().longValue();
		}
		
		return sum;
	}


	private List<KnightMove> validKeySequences() {
		List<KnightMove> tasks;
		if (key == null) { /* The first level key-press is any key, hence computing all keys */
			tasks = computeNextKeypadPresses(Arrays.asList(Key.values()));
		} else {
			tasks = computeNextKeypadPresses(key.adjacentKnightMoves());
		}
		return tasks;
	}


	private List<KnightMove> computeNextKeypadPresses(List<Key> nextValidMoves) {
		List<KnightMove> tasks = new ArrayList<>();
		for (Key adjMove : nextValidMoves) {			
			KnightMove task = nextKightMoveIfAllowed(adjMove);
			if (task == null) continue;
			task.fork();			
			tasks.add(task);
		}
		
		return tasks;
	}
	
	/**
	 * Next Knight move considers existing moves (or key sequences),
	 * which altogether allows a specified maximum vowels in a sequence.
	 * 
	 * @param adjMove the next key being considered.
	 * @return instance of a Knight move KnightMove or <code>null</code> if the move
	 *  or key-press is not allowed.
	 */
	private KnightMove nextKightMoveIfAllowed(Key adjMove){
		int newVowelCount = vowelCount;
		if (adjMove.isVowel()) {
			if (!(vowelCount > 0)) 
				return null;
			
			newVowelCount = (vowelCount <= 0) ? 0 : vowelCount - 1;
		}
		
		return new KnightMove(n-1, adjMove, cache, newVowelCount);
	}

}
