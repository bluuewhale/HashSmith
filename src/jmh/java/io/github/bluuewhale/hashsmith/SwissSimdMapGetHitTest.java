package io.github.bluuewhale.hashsmith;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
@Warmup(iterations = 10, time = 2)
@Measurement(iterations = 10, time = 2)
// Benchmark adapted from Eclipse Collections mutable map get test.
public class SwissSimdMapGetHitTest {

	private static final int RANDOM_COUNT = 9;

	@Param({ "250000", "500000", "750000", "1000000", "1250000", "1500000", "1750000", "2000000", "2250000", "2500000", "2750000", "3000000",
			"3250000", "3500000", "3750000", "4000000", "4250000", "4500000", "4750000", "5000000", "5250000", "5500000", "5750000", "6000000",
			"6250000", "6500000", "6750000", "7000000", "7250000", "7500000", "7750000", "8000000", "8250000", "8500000", "8750000", "9000000",
			"9250000", "9500000", "9750000", "10000000" })
	public int size;

	private String[] elements;
	private SwissSimdMap<String, String> swissMap;

	private String randomNumeric(Random random) {
		StringBuilder sb = new StringBuilder(RANDOM_COUNT);
		for (int i = 0; i < RANDOM_COUNT; i++) {
			sb.append((char) ('0' + random.nextInt(10)));
		}
		return sb.toString();
	}

	@Setup
	public void setUp() {
		this.elements = new String[this.size];
		this.swissMap = new SwissSimdMap<>(this.size);
		Random random = new Random(123456789012345L);
		for (int i = 0; i < this.size; i++) {
			String element = randomNumeric(random);
			this.elements[i] = element;
			this.swissMap.put(element, "dummy");
		}
	}

//	@Benchmark
	public void get() {
		int localSize = this.size;
		String[] localElements = this.elements;
		SwissSimdMap<String, String> localSwissMap = this.swissMap;
		for (int i = 0; i < localSize; i++) {
			if (localSwissMap.get(localElements[i]) == null) {
				throw new AssertionError(i);
			}
		}
	}
}

