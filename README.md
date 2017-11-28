Extras for Java 8+ Streams
==========================

[![Build Status](https://travis-ci.org/ataylor284/javastream-extras.png?branch=master)](https://travis-ci.org/ataylor284/javastream-extras)

This provides some extra collectors to use with Java 8+ streams.

The collectors provided are:

1. RandomValueCollector - returns one random value from from the
stream.  Each item has an equal probability of being returned.  If the
stream is empty, Optional.empty() is returned.

2. TopNCollector - returns the top N values in the stream.  If the
stream contains fewer than N items, the entire stream is returned. The
stream value type must implement the Comparable interface.

# Code Examples

     // collect one value from the stream where each value has equal
     // probability of being chosen
     IntStream.range(1, 20).boxed().collect(new RandomValueCollector())

     // collect only the top 5 values using the objects natural ordering
     IntStream.range(1, 20).boxed().collect(new TopNCollector(5))
