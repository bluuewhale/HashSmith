package com.donghyungko.swisstable;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RobinHoodMapSmokeTest {

	@Test
	void smokeLargeInsertDeleteReinsert() {
		// given
		var m = new RobinHoodMap<Integer, Integer>();
		int n = 100_000;

		// when: insert n
		for (int i = 0; i < n; i++) m.put(i, i * 2);

		// expect
		for (int i = 0; i < n; i++) assertEquals(i * 2, m.get(i));

		// when: delete half (even)
		for (int i = 0; i < n; i += 2) m.remove(i);

		// expect: evens gone, odds still present
		for (int i = 0; i < n; i++) {
			Integer v = m.get(i);
			if (i % 2 == 0) assertNull(v);
			else assertEquals(i * 2, v);
		}

		// when: reinsert evens with new values
		for (int i = 0; i < n; i += 2) m.put(i, i * 3);

		// expect: final consistency
		for (int i = 0; i < n; i++) {
			int expected = (i % 2 == 0) ? i * 3 : i * 2;
			assertEquals(expected, m.get(i));
		}
		assertEquals(n, m.size());
	}

	@Test
	void smokeHighCollisionLoop() {
		// given keys with identical hashCode
		record Collide(int v) { @Override public int hashCode() { return 0; } }
		var m = new RobinHoodMap<Collide, Integer>();
		int n = 10_000;

		// when: insert
		for (int i = 0; i < n; i++) m.put(new Collide(i), i);

		// expect: retrieve
		for (int i = 0; i < n; i++) assertEquals(i, m.get(new Collide(i)));

		// when: delete some
		for (int i = 0; i < n; i += 3) m.remove(new Collide(i));

		// expect: deleted keys gone, others present
		for (int i = 0; i < n; i++) {
			Integer v = m.get(new Collide(i));
			if (i % 3 == 0) assertNull(v);
			else assertEquals(i, v);
		}
	}
}

