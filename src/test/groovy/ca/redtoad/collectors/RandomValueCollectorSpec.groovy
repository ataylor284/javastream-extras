package ca.redtoad.collectors

import spock.lang.Specification

class RandomValueCollectorSpec extends Specification {

    def 'Collecting empty stream should return empty optional.'() {
        when:
        def stream = [].stream()
        def collector = new RandomValueCollector()

        then:
        stream.collect(collector) == Optional.empty()
    }

    def 'Collecting single value stream should return that value.'() {
        when:
        def stream = ['hello'].stream()
        def collector = new RandomValueCollector()

        then:
        stream.collect(collector) == Optional.of('hello')
    }

    def 'Collecting multiple value stream should return every unique value.'() {
        when:
        def items = ['foo', 'bar', 'blat', 'greep'] as Set
        def collector = new RandomValueCollector()

        then:
        def seen = [] as Set
        for (i in 0..<10000) {
            seen << items.stream().collect(collector).get()
            if (seen == items) {
                break
            }
        }
        seen == items
    }

    def 'Collecting multiple value stream in parallel should return every unique value.'() {
        when:
        def items = ['foo', 'bar', 'blat', 'greep'] as Set
        def collector = new RandomValueCollector()

        then:
        def seen = [] as Set
        for (i in 0..<10000) {
            seen << items.parallelStream().collect(collector).get()
            if (seen == items) {
                break
            }
        }
        seen == items
    }

    def 'Collecting with custom random provider should return predictable results.'() {
        when:
        def stream = ['foo', 'bar', 'blat', 'greep'].stream()
        def collector = new RandomValueCollector(new Random() {
            int nextInt(int range) { return 0; }
        })

        then:
        stream.collect(collector) == Optional.of('greep')
    }
}