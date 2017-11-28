package ca.redtoad.collectors;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collector;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Collects one value from the stream at random, where each value has
 * an equal probability of being chosen.
 */
public class RandomValueCollector<T> implements Collector<T, RandomValueCollector.Accumulator<T>, Optional<T>> {

    private final Random rand;

    /**
     * Constructs new RandomValueCollector that uses a
     * {@link java.util.Random} as a source of randomness.
     */
    public RandomValueCollector() {
        this.rand = new Random();
    }

    /**
     * Constructs new RandomValueCollector that uses the provided
     * source of randomness.
     * @param rand the source of randomness
     */
    public RandomValueCollector(Random rand) {
        this.rand = rand;
    }

    static class Accumulator<T> {
        private final Random rand;
        private int count;
        private T value;

        private Accumulator(Random rand) {
            this.rand = rand;
        }

        void consume(T item) {
            if (item == null) {
                throw new NullPointerException();
            }
            // accept next item as the selected item with a probability of (1/count)
            // this gives 1/1 for item 0, 1/2 for item 1, 1/3 for item 2, etc.
            count += 1;
            if (rand.nextInt(count) == 0) {
                value = item;
            }
        }

        Accumulator<T> combine(Accumulator<T> other) {
            if (rand.nextInt(count + other.count) >= count) {
                value = other.value;
            }
            count += other.count;
            return this;
        }

        Optional<T> finish() {
            return Optional.ofNullable(value);
        }
    }

    @Override
    public Supplier<Accumulator<T>> supplier() {
        return () -> new Accumulator<>(rand);
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
    public Function<Accumulator<T>, Optional<T>> finisher() {
        return Accumulator::finish;
    }

    @Override
    public Set<Collector.Characteristics> characteristics() {
        return EnumSet.noneOf(Collector.Characteristics.class);
    }
}
