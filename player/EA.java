import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sebastien on 4-10-16.
 */
public class EA {

    private final SELECTION_TYPES selectionType;
    private MUTATION_TYPE mutationType;
    private final double selectionPressure;
    private final int numParents;

    private final double pMutate = 0.5;

    public enum SELECTION_TYPES {
        UNIFORM, TOURNAMENT, ROULETTE, STOCHASTIC
    }

    public enum MUTATION_TYPE {
        REINIT, GAUSSIAN_NOISE
    }

    public EA(SELECTION_TYPES selectionType, MUTATION_TYPE mutationType, double selectionPressure, int numParents){
        this.selectionType = selectionType;
        this.mutationType = mutationType;
        this.selectionPressure = selectionPressure;
        this.numParents = numParents;
    }

    public Population select(Population currentPopulation){
        Population selection = currentPopulation.getTopN(numParents);

        return selection;
    }

    public Population recombine(Population parents){
        ArrayList<Individual> children = new ArrayList<>(parents.getPopSize());
        for(int i=0; i < parents.getPopSize(); i++)
        {
            double values1[];
            double values2[];
            values1 = parents.getIndividual(i % parents.getPopSize()).getParameters();
            values2 = parents.getIndividual((i+1) % parents.getPopSize()).getParameters();
            //System.out.println(parents.getIndividual(i % parents.getPopSize()));
            //System.out.println(parents.getIndividual((i+1) % parents.getPopSize()));
            //System.out.println("-----------");
            double childValues[] = new double[values1.length];
            for(int j = 0; j < values1.length; j++){
                childValues[j] = (values1[j] + values2[j]) / 2;
            }
            children.add(new Individual(childValues));
        }
        Population childPop = new Population(children, parents.getEvaluation());
        return childPop;
    }

    public Population kill(Population population, Population children){
        ArrayList<Integer>killed = new ArrayList<>(children.getPopSize());
        for(int i = 0; i < children.getPopSize();i++){
            int toKill = -1;
            while(toKill == -1 || killed.contains(toKill)){
                toKill = (int )(Math.random() * population.getPopSize());
            }
            killed.add(toKill);
            population.getIndividual(toKill).replace(children.getIndividual(i).getParameters());
        }

        return population;
    }

    public Population mutation(Population population, double rate)
    {
        switch (mutationType)
        {
            case REINIT:
                for (int i = numParents; i < population.getPopSize(); i++)
                {
                    if (Math.random() < pMutate)
                    {
                        population.getIndividual(i).initParams();
                    }
                }
                break;

            case GAUSSIAN_NOISE:
                Random r = new Random();
                for (int i = numParents; i < population.getPopSize(); i++)
                {
                    if (Math.random() < pMutate)
                    {
                        Individual individual = population.getIndividual(i);
                        double params[] = individual.getParameters();
                        for (int j = 0; j < params.length; j++)
                        {
                            params[j] += rate * r.nextGaussian() / 3 * individual.range;
                            if (params[j] > individual.paramLimits[1]) {params[j] = individual.paramLimits[1];}
                            if (params[j] < individual.paramLimits[0]) {params[j] = individual.paramLimits[0];}
                        }
                    }
                }
        }
        return population;
    }
}
