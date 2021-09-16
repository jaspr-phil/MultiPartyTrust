/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaspr.provfsm.weka;

import java.io.Serializable;
import java.util.*;

import weka.attributeSelection.*;
import weka.core.Instances;

/**
 *
 * @author phil
 */
public class mRMR extends ASEvaluation implements Serializable, RedundancyComputer, AttributeEvaluator {

    Instances header;
    double[][] corrmatrix;

    private int numfeatures = -1;
    public void setNumFeatures(int numfeatures) {
        this.numfeatures = numfeatures;
    }
    private List<Integer> selected;
    double[] scores;
    int target;
    
    
    ASEvaluation baseEval = new CorrelationAttributeEval();
    public void setBaseEval(ASEvaluation eval) {
        this.baseEval = eval;
    }    
    
    IFunction<Double,Double> mrmrComputer = new IFunction<Double, Double>() {
        @Override
        public Double run(Double rel, Double red) {
               return rel-red;
        }
    };
    
    public void buildEvaluator(Instances data) throws Exception {
        this.header = new Instances(data,0,0);

        scores = new double[data.numAttributes()];
        Arrays.fill(scores, -Double.MAX_VALUE);
        this.selected = new ArrayList<>();
        
        this.target = data.classIndex();
        
        this.corrmatrix = new double[data.numAttributes()][];
        
        computemRMR(data);
        
        for (int a=0;a<data.numAttributes();a++) {
            if (!selected.contains(a)) {
                selected.add(a);
            }
        }
    }
    
    public void updateCorrelationMatrix(double[][] corrmatrix, Instances data, int t) throws Exception {
        if (corrmatrix[t] == null && data == null) {
            throw new Exception("Attempted to update correlation matrix with null dataset.");
        } else if (corrmatrix[t] == null && data != null) { // Then it needs updating.
            if (data.attribute(t).numValues()<=1) {
                corrmatrix[t] = new double[data.numAttributes()];
            } else {
               corrmatrix[t] = computeCorrelation(data, t);
            }
        }
    }
    
    private double[] computeCorrelation(Instances data, int target) throws Exception {        
        int oci = data.classIndex();
        data.setClassIndex(target);
                
        ASEvaluation atteval = ASEvaluation.makeCopies(baseEval, 1)[0];
        ((ASEvaluation) atteval).buildEvaluator(data);
        double[] correlation = new double[data.numAttributes()];
        for (int i=0;i<data.numAttributes();i++) {
            if (i==target) correlation[i] = 0;
            else correlation[i] = ((AttributeEvaluator)atteval).evaluateAttribute(i);
        }
        
        data.setClassIndex(oci);
        return correlation;
    }

    private void computemRMR(Instances data) throws Exception {
        //Update the correlation matrix for the target variable neccessary
        System.out.print("Computing relevancies...");
        updateCorrelationMatrix(corrmatrix, data, target);
        System.out.println("done.");

        int M = data.numAttributes();

        HashSet<Integer> unselected = new HashSet<>();
        for (int i=0;i<M;i++) {
            if (i!=target) {
                unselected.add(i);
            }
        }

        double totrel = 0.;
        double totred = 0.;

        int ss = 1;
        double maxscore = -Double.MAX_VALUE;
        double maxrel = -Double.MAX_VALUE;
        double maxred = -Double.MAX_VALUE;
        int maxi = Integer.MIN_VALUE;

        while (selected.size() < numfeatures && !unselected.isEmpty()) {

            for (int a : unselected) {
                double rel = totrel + corrmatrix[target][a];
                rel = rel / ss;

                double red = totred;
                for (int s : selected) {
                    red += corrmatrix[s][a];
                }
                red = red / ss*ss;

                double mrmr = mrmrComputer.run(rel, red);

                if (mrmr > maxscore) {
                    maxscore = mrmr;
                    maxrel = rel;
                    maxred = red;
                    maxi = a;
                }
            }

            ss++;
            totrel += maxrel;
            totred += maxred;
            unselected.remove(maxi);
            selected.add(maxi);
            scores[maxi] = maxscore;
            updateCorrelationMatrix(corrmatrix, data, maxi);

        }
    }
    
    /***
     * Scores may not necessarily be in the correct order (due to negatives and selection scores), so the mRMR scores are not returned here.
     * Instead, the order of selection determines the score: earlier selection -> higher score.
     */
    @Override
    public double evaluateAttribute(int attribute) {
        if (!selected.contains(attribute)) return 0;
        double ret = selected.size() - selected.indexOf(attribute);
        return ret / (double)selected.size();
    }
    
    @Override
    public int[] rankForAtt(int attribute) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getCorrelation(int a, int t) {
//        System.out.println(baseEval+", "+a+", "+t+", "+corrmatrix[a][t]);
        return corrmatrix[a][t];
    }
    
    public String toString() {
        return baseEval.getClass().getName()+"_mRMR";
    }
    
}
