/**
 * Created by sebastien on 4-10-16.
 */


public class Individual {

    int paramSize = 10;
    int paramLimits[] = {-5, 5};
    int range = paramLimits[1] - paramLimits[0];

    private double fitness = 0;

    private double parameters[] = new double[paramSize];

    public Individual(double params[]){
        parameters = params;
    }

    public Individual()
    {
        initParams();
    }

    public void initParams()
    {
        for (int i = 0; i < paramSize; i++)
        {
            parameters[i] = Math.random() * (paramLimits[1] - paramLimits[0]) + paramLimits[0];
        }
    }

    public double[] getParameters() {
        return parameters;
    }

    public void setParameters(double[] parameters) {
        this.parameters = parameters;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public String toString()
    {
        String params = "";
        for(double param : parameters){
            params+= param + ",";
        }
        return "Fitness: " + fitness + " (" + params + ")";
    }

    public void replace(double params[]){
        parameters = params;
    }
}
