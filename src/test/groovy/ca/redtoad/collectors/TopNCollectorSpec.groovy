package ca.redtoad.collectors

import spock.lang.Specification

class TopNCollectorSpec extends Specification {

    def 'Collecting empty stream should return empty stream.'() {
        when:
        def stream = [].stream()
        def collector = new TopNCollector(10)

        then:
        stream.collect(collector) == []
    }

    def 'Collecting top 3 from length 10 stream should return top 3 items.'() {
        when:
        def stream = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10].stream()
        def collector = new TopNCollector(3)

        then:
        stream.collect(collector) == [8,9,10]
    }

    def 'Collecting top 5 from length 3 stream should return top 3 items.'() {
        when:
        def stream = [1, 2, 3].stream()
        def collector = new TopNCollector(5)

        then:
        stream.collect(collector) == [1,2,3]
    }

    def 'Collecting top 5 from shuffled length 100 stream should return top 5 items.'() {
        when:
        def list = (0..<100).toList()
        Collections.shuffle(list)
        println list
        def stream = list.stream()
        def collector = new TopNCollector(5)

        then:
        stream.collect(collector) == [95, 96, 97, 98, 99]
    }

    def 'Collecting top 5 from shuffled length 100 parallel stream should return top 5 items.'() {
        when:
        def list = (0..<100).toList()
        Collections.shuffle(list)
        println list
        def stream = list.parallelStream()
        def collector = new TopNCollector(5)

        then:
        stream.collect(collector) == [95, 96, 97, 98, 99]
    }
}
