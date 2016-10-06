import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Properties;

public class player36 implements ContestSubmission
{
    private static final int populationSize = 100;

    Population population;
    Random rnd_;
    ContestEvaluation evaluation_;
    EA algorithm;
    private int evaluations_limit_;
    private int maxIterations;
    double selectionPressure = 1;
    int numParents = populationSize/10;
	
	public player36()
    {
        algorithm = new EA(EA.SELECTION_TYPES.UNIFORM, 1.5,10);
		rnd_ = new Random();
        //algorithm = new EA(EA.SELECTION_TYPES.UNIFORM, selectionPressure, numParents);
	}

	public void setSeed(long seed)
	{
		// Set seed of algorithms random process
		rnd_.setSeed(seed);
	}

	public void setEvaluation(ContestEvaluation evaluation)
	{
		// Set evaluation problem used in the run
		evaluation_ = evaluation;

		// Get evaluation properties
		Properties props = evaluation.getProperties();
        // Get evaluation limit
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
        maxIterations = evaluations_limit_/populationSize;
		// Property keys depend on specific evaluation
		// E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

		// Do sth with property values, e.g. specify relevant settings of your algorithm
        if(isMultimodal){
            // Do sth
        }else{
            // Do sth else
        }
    }

	public void run()
    {
        int its = 0;
        population = new Population(populationSize, evaluation_);
        Population selection;
        Population childeren;

        maxIterations = 100;

        while(its < maxIterations) {
            population.evaluate();

            selection = algorithm.select(population);


            childeren = algorithm.recombine(selection);

            population = algorithm.kill(population, childeren);

            System.out.println(population);
            its++;
        }
	}
}
