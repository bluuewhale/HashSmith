package io.github.bluuewhale.hashsmith;

import java.util.AbstractMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Shared array-backed Map boilerplate and utilities.
 */
abstract class AbstractArrayMap<K, V> extends AbstractMap<K, V> {

	protected int capacity;
	protected int size;
	protected int maxLoad;
	protected double loadFactor;

	protected AbstractArrayMap(int initialCapacity, double loadFactor) {
		validateLoadFactor(loadFactor);
		this.loadFactor = loadFactor;
		init(initialCapacity);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return findIndex(key) >= 0;
	}

	@Override
	public V get(Object key) {
		int idx = findIndex(key);
		return (idx >= 0) ? valueAt(idx) : null;
	}

	/* Hooks for subclasses */
	protected abstract void init(int initialCapacity);
	protected abstract int findIndex(Object key);
	protected abstract V valueAt(int idx);

	/* Common utilities */
	protected int calcMaxLoad(int cap) {
		int ml = (int) (cap * loadFactor);
		return Math.max(1, Math.min(ml, cap - 1));
	}

	protected int ceilPow2(int x) {
		if (x <= 1) return 1;
		return Integer.highestOneBit(x - 1) << 1;
	}

	protected void validateLoadFactor(double lf) {
		if (!(lf > 0.0d && lf < 1.0d)) {
			throw new IllegalArgumentException("loadFactor must be in (0,1): " + lf);
		}
	}

	protected int hashNonNull(Object key) {
		if (key == null) throw new NullPointerException("Null keys not supported");
		return Hashing.smearedHash(key);
	}

	protected int hashNullable(Object key) {
		return Hashing.smearedHash(key);
	}

	/**
	 * (start, step) generator to visit every slot in a power-of-two table.
	 */
	protected static final class RandomCycle {
		final int start;
		final int step;
		final int mask;

		RandomCycle(int capacity) {
			if (capacity <= 0 || (capacity & (capacity - 1)) != 0) {
				throw new IllegalArgumentException("capacity must be a power of two");
			}
			this.mask = capacity - 1;
			ThreadLocalRandom r = ThreadLocalRandom.current();
			this.start = r.nextInt() & mask;
			this.step = r.nextInt() | 1; // odd step → full-cycle walk
		}

		int indexAt(int iteration) {
			return (start + (iteration * step)) & mask;
		}
	}
}

