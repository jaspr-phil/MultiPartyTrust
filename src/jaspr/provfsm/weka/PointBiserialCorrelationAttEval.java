package jaspr.provfsm.weka;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.AttributeEvaluator;
import weka.core.Instance;
import weka.core.Instances;

public class PointBiserialCorrelationAttEval extends ASEvaluation implements AttributeEvaluator {

    double[] scores;

    public PointBiserialCorrelationAttEval() {
        super();
    }

    // This should be equivalbent to Pearson but my code doesn't work quite right.


    @Override
    public void buildEvaluator(Instances data) throws Exception {
        scores = new double[data.numAttributes()];

        double popCount = data.numInstances();
        double popMean = 0;
        double popMean2 = 0;
        double[][] means = new double[data.numAttributes()][2];
        double[][] counts = new double[data.numAttributes()][2];

        for (Instance inst : data) {
            popMean += inst.classValue();
            popMean2 += inst.classValue()*inst.classValue();
        }

        for (int a=0;a<data.size();a++) {
            if (data.classIndex() == a) {
                continue;
            }

            for (Instance inst : data) {
                means[a][(int)inst.value(a)] += inst.classValue();
                ++counts[a][(int)inst.value(a)];
            }
        }

        double popStd = Math.sqrt(popMean2 - popMean*popMean);

        for (int a = 0;a<data.size();a++) {
            means[a][0] /= counts[a][0];
            means[a][1] /= counts[a][1];

            scores[a] = (Math.abs(means[a][0] - means[a][1]) / popStd) * Math.sqrt(counts[a][0]*counts[a][1] / (popCount*(popCount-1)));
        }
    }

    @Override
    public double evaluateAttribute(int attribute) throws Exception {
        return scores[attribute];
    }
}
