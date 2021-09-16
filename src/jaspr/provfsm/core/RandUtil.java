//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class RandUtil {
    public static Random rand;

    public RandUtil() {
    }

    public static void init() {
        rand = ThreadLocalRandom.current();
    }

    public static <E> List<E> sampleLikelihood(List<E> list, float n) {
        int length = list.size();
        return sample(list, (int)Math.ceil((double)((float)length * n)));
    }

    public static <E> List<E> samplewr(List<E> list, int n) {
        ArrayList ret = new ArrayList();

        while(ret.size() < n) {
            ret.add(choose(list));
        }

        return ret;
    }

    public static <E> List<E> sample(List<E> list, int n) {
        int length = list.size();
        if (length < n) {
            n = length;
        }

        for(int i = length - 1; i >= length - n; --i) {
            Collections.swap(list, i, rand.nextInt(i + 1));
        }

        return list.subList(length - n, length);
    }

    public static <E> E choose(List<E> list) {
        return list.get(rand.nextInt(list.size()));
    }

    public static <E> E choose(E[] list) {
        return list[rand.nextInt(list.length)];
    }

    public static <E> E choose(Set<E> set) {
        int index = rand.nextInt(set.size());
        Iterator<E> iter = set.iterator();

        for(int i = 0; i < index; ++i) {
            iter.next();
        }

        return iter.next();
    }

    public static int randInt(int lower, int upper) {
        return rand.nextInt(upper - lower) + lower;
    }

    public static double randDouble(double lower, double upper) {
        return (upper - lower) * rand.nextDouble() + lower;
    }

    public static double randGaussian(double mu, double sigma) {
        return mu + rand.nextGaussian() * sigma;
    }

    public static boolean randBoolean(double probTrue) {
        return rand.nextDouble() < probTrue;
    }

    public static Set<Integer> randIntsUniq(int length, int min, int max) {
        if (max - min < length) {
            throw new IllegalArgumentException("The range of the list must be larger than or equal to the length.");
        } else {
            Set<Integer> ret = new HashSet();

            for(int i = 0; i < length; ++i) {
                int r;
                do {
                    r = randInt(min, max);
                } while(ret.contains(r));

                ret.add(r);
            }

            return ret;
        }
    }

    public static List<Double> randGaussians(int length, double mu, double sigma) {
        List<Double> ret = new ArrayList();

        for(int i = 0; i < length; ++i) {
            ret.add(randGaussian(mu, sigma));
        }

        return ret;
    }

    public static List<Double> randDoubles(int length, double min, double max) {
        List<Double> ret = new ArrayList();

        for(int i = 0; i < length; ++i) {
            ret.add(randDouble(min, max));
        }

        return ret;
    }
}
