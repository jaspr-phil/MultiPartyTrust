/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaspr.provfsm.weka;

import java.io.Serializable;


public abstract class IFunction<I,O> implements Serializable {
    
    public abstract O run(I a, I b);
    
}
