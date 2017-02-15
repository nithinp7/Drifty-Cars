
package ai;

import static util.Constants.TIMESTEP;

/**
 *
 * @author Nithin
 */
public final class PID {
    
    private final float prop_const, deriv_const, integ_const;
    private float prop = 0, deriv = 0, integ = 0;
    
    public PID(float a, float b, float c) {
        prop_const = a;
        deriv_const = b;
        integ_const = c;
    }
    
    public float update(float prop) {
        return update(prop, (prop - this.prop)/TIMESTEP);
    }
    
    public float update(float prop, float deriv) {
        this.prop = prop;
        this.deriv = deriv;
        integ += prop*TIMESTEP;
        
        return prop_const*prop + deriv_const*deriv + integ_const*integ;
    }
    
    public void clearIntegralAccumulation() {
        integ = 0;
    }
}
