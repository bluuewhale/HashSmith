# RobinHoodMap (HashSmith)

> Open-addressing map with Robin Hood hashing for predictable probe lengths.

## Overview
- Robin Hood insertion swaps to even out probe distances, reducing long clusters.
- Backward-shift deletion keeps clusters tight after removals.
- Null keys are **not** allowed; null values are allowed.
- Default load factor 0.75 with power-of-two capacity growth.

## Requirements
- JDK 21+ (project toolchain); no Vector API dependency.
- Gradle (wrapper provided).

## Quick Start
```java
import io.github.bluuewhale.hashsmith.RobinHoodMap;

public class Demo {
    public static void main(String[] args) {
        var map = new RobinHoodMap<String, Integer>();
        map.put("a", 1);
        map.put("b", 2);
        map.remove("a");
        map.put("c", 3);
        System.out.println(map.get("b")); // 2
    }
}
```

## Highlights
- **Robin Hood insertion**: swaps with shorter-distance occupants to cap worst-case probe lengths.
- **Backward-shift delete**: closes gaps to maintain contiguous clusters after removals.
- **Open addressing**: contiguous arrays for keys/values/probe distances; no node allocations.
- **Resizing**: power-of-two growth with recalculated max load (`loadFactor * capacity`).

## Design Notes
- Probe distance array tracks how far each entry is from its ideal bucket.
- Hash smearing mirrors `java.util.HashMap` style to spread hash codes.
- Null keys throw `NullPointerException`; values can be null.
- Collision resilience validated via tests with colliding hashes (see `RobinHoodMapSmokeTest`).

## Build & Test
```bash
./gradlew build        # full build
./gradlew test         # JUnit 5 tests
```

## Tips
- Works well for workloads needing predictable probe lengths without SIMD support.
- For vectorized probing and null-key support, see `SwissSimdMap`.
