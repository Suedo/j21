package org.playground.patternMatch;

import java.util.Collections;
import java.util.List;

public class Record101 {

    // cannot extend anything,
    // is a final class, so nothing can extend it
    // However, a record can implement other interfaces
    record Range(int begin, int end, List<String> values) {

        // no instance variables, only static variable possible
        static String name = "RangeRecord";

        // Canonical Constructor: The constructor with all the arguments of a record
        // modifying the canonical constructor
        Range(int begin, int end, List<String> values) {
            this.begin = begin;
            this.end = end;

            // benefit #1 : add defense mechanism,
            // prevents that caller from having a reference to a mutable internal data of a record
            // this creates a copy, thus a different ref, which the caller doesn't know, and thus can no longer modify state of the record
            this.values = List.copyOf(values);

            // benefit 2
            // add custom validation
            if (end < begin) {
                throw new IllegalStateException("End should be larger than beginning");
            }

        }

        /*
        // shorter representation of the above canonical construction
        -----------------------
        Range {
            // logic..
        }
         */

        Range(int upto) {
            // all non-canonical constructors MUST call the canonical constructor
            this(0, upto, Collections.emptyList());
        }


        // can define methods which are public by default
        void print() {
            System.out.println(String.format("I, %s, range from %s to %s", name, begin(), end()));
        }
    }

}
