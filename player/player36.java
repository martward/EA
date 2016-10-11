import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Properties;

public class player36 implements ContestSubmission
{

    private static int populationSize = 50;

    Population population;
    Random rnd_;
    ContestEvaluation evaluation_;
    EA algorithm;
    private int evaluations_limit_;
    private int maxIterations;
    double selectionPressure = 1.5;
    int numParents;
    int numChildren;
    double pMutate;
    boolean isMultimodal;
	
	public player36()
    {
		rnd_ = new Random();
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
		// Property keys depend on specific evaluation
		// E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));
		// Do sth with property values, e.g. specify relevant settings of your algorithm
        populationSize = 76;
        pMutate = 0.25;
        numParents = (int) populationSize/2;
        int numChildren = 3* numParents;
        maxIterations = evaluations_limit_/populationSize-1;

        algorithm = new EA(EA.SELECTION_TYPES.LINEAR_RANK,
                EA.MUTATION_TYPE.GAUSSIAN_NOISE,
                EA.RECOMBINATION_TYPES.WHOLE_ARITHMETIC,
                EA.KILL_TYPE.WORST,
                1.3,numParents, numChildren,pMutate
        );
    }

	public void run()
    {
        System.out.println(populationSize);
        int its = 0;
        population = new Population(populationSize, evaluation_);
        population.evaluate();
        Population selection;
        Population children;
        double rate;
        while(its < maxIterations && population.getIndividual(0).getFitness() < 10.0) {
            rate = 1. - (double)its/(double)maxIterations;


            selection = algorithm.select(population);

            children = algorithm.recombine(selection);

            population = algorithm.kill(population, children, populationSize);

            population = algorithm.mutation(population, rate);

            population.evaluate();

            //System.out.println(population.getIndividual(0));

            its++;
        }
        System.out.println(its);
	}
}
