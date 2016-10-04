import java.util.Random;

/**
 * Created by sebastien on 4-10-16.
 */
public class Individual {

    int paramSize = 0;
    int paramLimits[] = {-50000, 50000};

    private double parameters[] = new double[paramSize];

    public Individual()
    {
        initParams();
    }

    private void initParams()
    {
        for (int i = 0; i < paramSize; i++)
        {
            parameters[i] = Math.random() * paramLimits[1] - paramLimits[0] + paramLimits[0];
        }
    }

    public double[] getParameters() {
        return parameters;
    }

    public void setParameters(double[] parameters) {
        this.parameters = parameters;
    }
}
