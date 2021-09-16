//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.core;

import jaspr.provfsm.models.Model;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class Util {
    static DecimalFormat[] df = new DecimalFormat[]{new DecimalFormat("0."), new DecimalFormat("0.0"), new DecimalFormat("0.00"), new DecimalFormat("0.000"), new DecimalFormat("0.0000")};

    public Util() {
    }

    public static List<Double> zeros(int num) {
        List<Double> list = new ArrayList();

        for(int i = 0; i < num; ++i) {
            list.add(0.0D);
        }

        return list;
    }

    public static List<Double> ones(int num) {
        List<Double> list = new ArrayList();

        for(int i = 0; i < num; ++i) {
            list.add(1.0D);
        }

        return list;
    }

    public static String format(double d) {
        return format(d, 3);
    }

    public static String formats(String sep, double... d) {
        StringBuilder sb = new StringBuilder();
        double[] var3 = d;
        int var4 = d.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            double x = var3[var5];
            sb.append(format(x)).append(sep);
        }

        return sb.toString();
    }

    public static String format(double d, int ndp) {
        return df[ndp].format(d);
    }

    public static double bound(double bot, double top, double number) {
        return Math.max(bot, Math.min(top, number));
    }

    public static String format(Collection<Double> ds) {
        StringBuilder sb = new StringBuilder("[");
        Iterator var2 = ds.iterator();

        while(var2.hasNext()) {
            double d = (Double)var2.next();
            sb.append(format(d)).append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    public static String format(Object data) {
        if (data instanceof Double) {
            return format((Double)data);
        } else {
            return data instanceof Float ? format((Double)data) : data.toString();
        }
    }

    public static <K, V> String format(Map<K, V> map) {
        StringBuilder sb = new StringBuilder("[");
        Iterator var2 = map.keySet().iterator();

        while(var2.hasNext()) {
            K k = var2.next();
            sb.append(format(k)).append("->").append(format(map.get(k))).append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    public static String format(String[] ds) {
        if (ds.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder("[");
        String[] var2 = ds;
        int var3 = ds.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String d = var2[var4];
            sb.append(d).append(", ");
        }

        sb.deleteCharAt(sb.length() - 2);
        sb.append("]");
        return sb.toString();
    }

    public static double max(Collection<Double> arr) {
        double maxVal = -1.7976931348623157E308D;
        Iterator var3 = arr.iterator();

        while(var3.hasNext()) {
            double d = (Double)var3.next();
            if (d > maxVal) {
                maxVal = d;
            }
        }

        return maxVal;
    }

    public static double sum(Collection<Double> arr) {
        double tot = 0.0D;

        double d;
        for(Iterator var3 = arr.iterator(); var3.hasNext(); tot += d) {
            d = (Double)var3.next();
        }

        return tot;
    }

    public static double min(Collection<Double> arr) {
        double maxVal = 1.7976931348623157E308D;
        Iterator var3 = arr.iterator();

        while(var3.hasNext()) {
            double d = (Double)var3.next();
            if (d < maxVal) {
                maxVal = d;
            }
        }

        return maxVal;
    }

    public static <T> Map<T, Double> normalizeMap(Map<T, Double> arr, double defaultvalue) {
        double max = max(arr.values());
        double min = min(arr.values());
        Map<T, Double> ret = new HashMap();
        Iterator var8;
        Object x;
        if (max == min) {
            var8 = arr.keySet().iterator();

            while(var8.hasNext()) {
                x = var8.next();
                ret.put(x, defaultvalue);
            }
        } else {
            var8 = arr.keySet().iterator();

            while(var8.hasNext()) {
                x = var8.next();
                if (Double.isNaN((Double)arr.get(x))) {
                    ret.put(x, 0.0D);
                } else {
                    ret.put(x, ((Double)arr.get(x) - min) / (max - min));
                }
            }
        }

        return ret;
    }

    public static Map<Model, List<Evaluation>> evaluate(Map<Model, List<Double>> predictions, List<Double> values, int learningInterval) throws Exception {
        ArrayList<Attribute> atts = new ArrayList();
        atts.add(new Attribute("Target"));
        Instances dummy = new Instances("dummy", atts, 0);
        dummy.setClassIndex(0);
        Map<Model, List<Evaluation>> evals = new HashMap();
        Iterator var6 = predictions.keySet().iterator();

        while(var6.hasNext()) {
            Model model = (Model)var6.next();
            Evaluation eval = new Evaluation(dummy);
            evals.put(model, new ArrayList());
            Iterator<Double> predIter = ((List)predictions.get(model)).iterator();
            Iterator<Double> valueIter = values.iterator();
            int simid = 0;

            do {
                if (simid % learningInterval == learningInterval - 1) {
                    ((List)evals.get(model)).add(eval);
                    eval = new Evaluation(dummy);
                }

                double value = (Double)valueIter.next();
                double prediction = (Double)predIter.next();
                Instance dummySample = new DenseInstance(1.0D, new double[]{value});
                dummySample.setDataset(dummy);
                eval.evaluateModelOnce(prediction, dummySample);
                ++simid;
            } while(predIter.hasNext() && valueIter.hasNext());

            ((List)evals.get(model)).add(eval);
        }

        return evals;
    }

    public static <K, V> Map<K, List<V>> flatGroupMapKeys(List<Map<K, List<V>>> dic) throws IllegalAccessException, InstantiationException {
        if (dic.isEmpty()) {
            return new HashMap();
        } else {
            Map ret = (Map)((Map)dic.get(0)).getClass().newInstance();
            Iterator var2 = ((Map)dic.get(0)).keySet().iterator();

            while(var2.hasNext()) {
                K k = var2.next();
                ret.put(k, dic.getClass().newInstance());
            }

            var2 = dic.iterator();

            while(var2.hasNext()) {
                Map<K, List<V>> x = (Map)var2.next();
                Iterator var4 = x.keySet().iterator();

                while(var4.hasNext()) {
                    K k = var4.next();
                    ((List)ret.get(k)).addAll((Collection)x.get(k));
                }
            }

            return ret;
        }
    }

    public static <K, V> Map<K, List<V>> groupMapKeys(List<Map<K, V>> dic) throws IllegalAccessException, InstantiationException {
        if (dic.isEmpty()) {
            return new HashMap();
        } else {
            Map ret = (Map)((Map)dic.get(0)).getClass().newInstance();
            Iterator var2 = ((Map)dic.get(0)).keySet().iterator();

            while(var2.hasNext()) {
                K k = var2.next();
                ret.put(k, dic.getClass().newInstance());
            }

            var2 = dic.iterator();

            while(var2.hasNext()) {
                Map<K, V> x = (Map)var2.next();
                Iterator var4 = x.keySet().iterator();

                while(var4.hasNext()) {
                    K k = var4.next();
                    ((List)ret.get(k)).add(x.get(k));
                }
            }

            return ret;
        }
    }

    public static Map<Model, Evaluation> evaluateAll(Map<Model, List<Double>> predictions, List<Double> values, boolean isBinary) throws Exception {
        ArrayList<Attribute> atts = new ArrayList();
        if (isBinary) {
            List<String> binaryValues = new ArrayList();
            binaryValues.add("F");
            binaryValues.add("T");
            atts.add(new Attribute("Target", binaryValues));
        } else {
            atts.add(new Attribute("Target"));
        }

        Instances dummy = new Instances("dummy", atts, 0);
        dummy.setClassIndex(0);
        Map<Model, Evaluation> evals = new HashMap();
        Iterator var6 = predictions.keySet().iterator();

        while(var6.hasNext()) {
            Model model = (Model)var6.next();
            Evaluation eval = new Evaluation(dummy);
            Iterator<Double> predIter = ((List)predictions.get(model)).iterator();
            Iterator valueIter = values.iterator();

            do {
                double value = (Double)valueIter.next();
                double prediction = (Double)predIter.next();
                Instance dummySample = new DenseInstance(1.0D, new double[]{value});
                dummySample.setDataset(dummy);
                eval.evaluateModelOnce(prediction, dummySample);
            } while(predIter.hasNext() && valueIter.hasNext());

            evals.put(model, eval);
        }

        return evals;
    }

    public static String format(Evaluation eval) throws Exception {
        return "\t" + format(eval.correlationCoefficient()) + "\t" + format(eval.meanAbsoluteError()) + "\t" + format(eval.rootMeanSquaredError());
    }

    public static double mean(Collection<Double> arr) {
        double sum = 0.0D;

        double d;
        for(Iterator var3 = arr.iterator(); var3.hasNext(); sum += d) {
            d = (Double)var3.next();
        }

        return sum / (double)arr.size();
    }

    public static double var(Collection<Double> arr) {
        double mean = mean(arr);
        double ret = 0.0D;

        double d;
        for(Iterator var5 = arr.iterator(); var5.hasNext(); ret += (d - mean) * (d - mean)) {
            d = (Double)var5.next();
        }

        return ret / (double)(arr.size() - 1);
    }

    public static double[] toArray(Collection<Double> a) {
        double[] ret = new double[a.size()];
        int i = 0;

        double x;
        for(Iterator var3 = a.iterator(); var3.hasNext(); ret[i++] = x) {
            x = (Double)var3.next();
        }

        return ret;
    }

    public static double corr(Collection<Double> a, Collection<Double> b) {
        return Utils.correlation(toArray(a), toArray(b), a.size());
    }

    public static double std(Collection<Double> arr) {
        return Math.sqrt(var(arr));
    }
}
