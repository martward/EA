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
    double pMutate;
    boolean isMultimodal;
    boolean hasStructure;
    boolean isSeparable;
    int numChildren;
    boolean singleParamMode;
    int initMultiplier;

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
        hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

        System.out.println("Multi model: " + (isMultimodal ? "yes" : "no"));
        System.out.println("Structure: " + (hasStructure ? "yes" : "no"));
        System.out.println("Separable: " + (isSeparable ? "yes" : "no"));
		// Do sth with property values, e.g. specify relevant settings of your algorithm

        if (!isMultimodal)
        {
            populationSize = 50;
            numParents = 1;
            numChildren = populationSize - numParents;
            pMutate = 1.0;
            initMultiplier = 1;
            maxIterations = evaluations_limit_/populationSize-1;
            singleParamMode = false;

            algorithm = new EA(EA.SELECTION_TYPES.TOPN,
                    EA.MUTATION_TYPE.GAUSSIAN_NOISE,
                    EA.RECOMBINATION_TYPES.NPOINTCROSSOVER,
                    EA.KILL_TYPE.WORST,
                    2.0,numParents, numChildren,pMutate, singleParamMode);
        }
        else if(hasStructure)
        {
            //System.out.println("this");
            populationSize = 240;
            initMultiplier = 10;
            pMutate = 1.0;
            numParents = (populationSize/2);
            numChildren = numParents;
            maxIterations = (evaluations_limit_ - populationSize*(initMultiplier-1))/(populationSize + numChildren)-2;
            singleParamMode = false;
            algorithm = new EA(EA.SELECTION_TYPES.DISTANCE,
                    EA.MUTATION_TYPE.GAUSSIAN_NOISE,
                    EA.RECOMBINATION_TYPES.SINGLE_ARITHMETIC,
                    EA.KILL_TYPE.WORST,
                    2.0,numParents, numChildren,pMutate, singleParamMode);
        } else{
            populationSize = 120;
            initMultiplier = 10;
            pMutate = 1.0;
            numParents = (populationSize/2);
            numChildren = numParents;
            maxIterations = (evaluations_limit_ - populationSize*(initMultiplier-1))/(populationSize + numChildren)-2;
            singleParamMode = false;
            algorithm = new EA(EA.SELECTION_TYPES.DISTANCE,
                    EA.MUTATION_TYPE.GAUSSIAN_NOISE,
                    EA.RECOMBINATION_TYPES.SINGLE_ARITHMETIC,
                    EA.KILL_TYPE.WORST,
                    2.0,numParents, numChildren,pMutate, singleParamMode);
        }
    }

	public void run()
    {
        int its = 0;
        population = new Population(populationSize*initMultiplier, evaluation_);
        population.evaluate();
        population = population.getTopN(populationSize);
        Population selection;
        Population children;
        double rate;

        //maxIterations = 1;
        while(its < maxIterations) {
            //rate = 1. - (double)its/(double)maxIterations;
            if (singleParamMode)
            {
                rate = Math.pow(0.000000001, (double)(its%(maxIterations/10))/(double)(maxIterations/10));
            }else
            {
                rate = Math.pow(0.000001, (double)its/(double)maxIterations);

            }
            //System.out.println("Rate: " + rate);
            //System.out.println("pop size: " + populationSize);


            selection = algorithm.select(population);
            //System.out.println("Selection: ");
            //System.out.println(selection + "\n\n");


            children = algorithm.recombine(selection);
            //System.out.println(children.getPopSize());
            //System.out.println("Children: ");
            //System.out.println(children + "\n\n");

            population = algorithm.kill(population, children, selection);

            //System.out.println("After kill: ");
            //System.out.println(population + "\n\n");

            population = algorithm.mutation(population, rate);
            //System.out.println("New: ");
            //System.out.println(population + "\n\n");

            population.evaluate();


            if(its < maxIterations - 2 && isMultimodal )
            {
                population.fitnessSharing();
            }

            its++;
        }
        System.out.println("Number of iterations ran: " + its);
	    //System.out.println(population.toString());
    }
}
