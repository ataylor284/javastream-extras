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
 * Collects one value from the stream, where each value has equal
 * probability.
 */
public class RandomValueCollector<T> implements Collector<T, RandomValueCollector.Accumulator<T>, Optional<T>> {

    private Random rand;

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
            count += 1;
            if (rand.nextInt(count) == 0) {
                value = item;
            }
        }

        Accumulator<T> combine(Accumulator<T> other) {
            Accumulator<T> combined = new Accumulator<>(rand);
            combined.count = count + other.count;
            if (rand.nextInt(combined.count) < count) {
                combined.value = value;
            } else {
                combined.value = other.value;
            }
            return combined;
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
        return EnumSet.of(Collector.Characteristics.UNORDERED);
    }
}
