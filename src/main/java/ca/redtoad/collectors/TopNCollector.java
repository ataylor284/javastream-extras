package ca.redtoad.collectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collector;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Collects the top N values in the stream as ordered by natural
 * ordering.
 */
public class TopNCollector<T extends Comparable<? super T>> implements Collector<T, TopNCollector.Accumulator<T>, List<T>> {

    private final int n;

    /**
     * Constructs new TopNCollector.
     * @param n number of values to collect
     */
    public TopNCollector(int n) {
        this.n = n;
    }

    static class Accumulator<T extends Comparable<? super T>> {
        private final PriorityQueue<T> q = new PriorityQueue<>();
        private final int n;

        private Accumulator(int n) {
            this.n = n;
        }

        void consume(T item) {
            if (q.size() < n || item.compareTo(q.peek()) > 0) {
                q.offer(item);
            }
            if (q.size() > n) {
                q.poll();
            }
        }

        Accumulator<T> combine(Accumulator<T> other) {
            q.addAll(other.q);
            while (q.size() > n) {
                q.poll();
            }
            return this;
        }

        List<T> finish() {
            List<T> result = new ArrayList<>(q);
            Collections.sort(result);
            return result;
        }
    }

    @Override
    public Supplier<Accumulator<T>> supplier() {
        return () -> new Accumulator<>(n);
    }

    @Override
    public BiConsumer<Accumulator<T>, T> accumulator() {
        return Accumulator::consume;
    }

    @Override
    public BinaryOperator<Accumulator<T>> combiner() {
        return Accumulator::combine;
    }

    @Override
    public Function<Accumulator<T>, List<T>> finisher() {
        return Accumulator::finish;
    }

    @Override
    public Set<Collector.Characteristics> characteristics() {
        return EnumSet.of(Collector.Characteristics.UNORDERED);
    }
}
