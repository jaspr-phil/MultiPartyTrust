/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jaspr.provfsm.weka;


/**
 *
 * @author phil
 */
public interface RedundancyComputer {
    
    public abstract int[] rankForAtt(int attribute);
    public abstract double getCorrelation(int a, int t);
    
}
