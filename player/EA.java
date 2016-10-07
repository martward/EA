import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sebastien on 4-10-16.
 */
public class EA {

    private final SELECTION_TYPES selectionType;
    private final MUTATION_TYPE mutationType;
    private final double selectionPressure;
    private final KILL_TYPE killType;
    private final int numParents;
    private double pMutate;
    private int numChildren;

    private final RECOMBINATION_TYPES recombinationType;

    public enum SELECTION_TYPES {
        LINEAR_RANK, EXPONENTIAL_RANK, TOPN
    }

    public enum MUTATION_TYPE {
        REINIT, GAUSSIAN_NOISE
    }

    public enum RECOMBINATION_TYPES {
        SINGLE_ARITHMETIC, SIMPLE_ARITHMETIC, WHOLE_ARITHMETIC, NPOINTCROSSOVER, UNIFORMCROSSOVER
    }

    public enum KILL_TYPE {
        RANDOM, WORST
    }

    public EA(SELECTION_TYPES selectionType,
              MUTATION_TYPE mutationType,
              RECOMBINATION_TYPES recombinationType,
              KILL_TYPE killType,
              double selectionPressure, int numParents,
              int numChildren, double pMutate){

        this.selectionType = selectionType;
        this.mutationType = mutationType;
        this.killType = killType;
        this.selectionPressure = selectionPressure;
        this.numParents = numParents;
        this.numChildren = numChildren;
        this.recombinationType = recombinationType;
        this.pMutate = pMutate;
    }

    public Population select(Population currentPopulation){
        double[] probs = new double[currentPopulation.getPopSize()];
        Population selection;
        int[] ranks = new int[currentPopulation.getPopSize()];
        for(int i = 0; i < ranks.length; i++){
            ranks[i] =  currentPopulation.getPopSize() - (i+1);
        }
        switch(selectionType)
        {
            case LINEAR_RANK:
                probs = linearRank(ranks);
                selection = selectPairs(currentPopulation,probs);
                break;
            case EXPONENTIAL_RANK:
                probs = exponentialRank(ranks);
                selection = selectPairs(currentPopulation,probs);
                break;
            default:
                selection = currentPopulation.getTopN(numParents);
                break;
        }
        return selection;
    }

    public Population recombine(Population parents)
    {
        ArrayList<Individual> children = new ArrayList<>(numChildren);
        for(int i=0; i < numChildren; i+=2)
        {
            double parent1Values[] = parents.getIndividual(i % parents.getPopSize()).getParameters();
            double parent2Values[] = parents.getIndividual((i+1) % parents.getPopSize()).getParameters();
            double child1Values[] = parent1Values.clone();
            double child2Values[] = parent2Values.clone();

            switch(recombinationType)
            {
                case SINGLE_ARITHMETIC:
                    singleArithmetic(parent1Values,parent2Values,child1Values,child2Values);
                    break;
                case SIMPLE_ARITHMETIC:
                    simpleArithmetic(parent1Values,parent2Values,child1Values,child2Values);
                    break;
                case UNIFORMCROSSOVER:
                    uniformCrossover(parent1Values,parent2Values,child1Values,child2Values);
                    break;
                case NPOINTCROSSOVER:
                    nPointCrossover(parent1Values,parent2Values,child1Values,child2Values);
                    break;
                case WHOLE_ARITHMETIC:
                    wholeArithmetic(parent1Values,parent2Values,child1Values,child2Values);
                    break;
            }
            children.add(new Individual(child1Values));
            children.add(new Individual(child2Values));
        }
        return new Population(children, parents.getEvaluation());
    }

    public Population kill(Population population, Population children)
    {
        switch(killType)
        {
            case RANDOM:
                ArrayList<Integer>killed = new ArrayList<>(children.getPopSize());
                for(int i = 0; i < children.getPopSize();i++){
                    int toKill = -1;
                    while(toKill == -1 || killed.contains(toKill)){
                        toKill = (int )(Math.random() * population.getPopSize());
                    }
                    killed.add(toKill);
                    population.getIndividual(toKill).replace(children.getIndividual(i).getParameters());
                }
                break;
            case WORST:
                ArrayList<Individual> childrenIndividuals = children.getPopulation();
                ArrayList<Individual> populationIndividuals = population.getPopulation();
                for (int i = 0; i < childrenIndividuals.size(); i++)
                {
                    populationIndividuals.set(populationIndividuals.size()-i-1, childrenIndividuals.get(i));
                }
                break;
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

    private void singleArithmetic(double values1[], double values2[], double childValues1[], double childValues2[])
    {
        double alpha = Math.random();
        int i = (int) (Math.random() * values1.length);
        childValues1[i] = alpha * values2[i] + (1-alpha) * values1[i];
        childValues2[i] = alpha * values1[i] + (1-alpha) * values2[i];

    }

    private void simpleArithmetic(double parent1Values[], double parent2Values[], double child1Values[], double child2Values[])
    {
        double alpha = Math.random();
        int i = (int) (Math.random() * parent1Values.length);
        for(int j = i; j < parent1Values.length; j++)
        {
            child1Values[j] = alpha * parent2Values[j] + (1-alpha) * parent1Values[j];
            child2Values[j] = alpha * parent1Values[j] + (1-alpha) * parent2Values[j];
        }
    }

    private void wholeArithmetic(double parent1Values[], double parent2Values[], double child1Values[], double child2Values[])
    {
        double alpha = Math.random();
        for(int j = 0; j < parent1Values.length; j++)
        {
            child1Values[j] = alpha * parent2Values[j] + (1-alpha) * parent1Values[j];
            child2Values[j] = alpha * parent1Values[j] + (1-alpha) * parent2Values[j];
        }
    }

    private void uniformCrossover(double parent1Values[], double parent2Values[], double child1Values[], double child2Values[])
    {
        double alpha = Math.random();
        for(int j = 0; j < parent1Values.length; j++) {
            if (Math.random() < alpha)
            {
                child1Values[j] = parent1Values[j];
                child2Values[j] = parent2Values[j];
            } else
            {
                child1Values[j] = parent2Values[j];
                child2Values[j] = parent1Values[j];
            }
        }
    }

    private void nPointCrossover(double parent1Values[], double parent2Values[], double child1Values[], double child2Values[])
    {
        int i = (int) (Math.random() * parent1Values.length);
        double alpha = Math.random();
        for(int j = i; j < parent1Values.length; j++) {
            if (Math.random() < alpha)
            {
                child1Values[j] = parent1Values[j];
                child2Values[j] = parent2Values[j];
            } else
            {
                child1Values[j] = parent2Values[j];
                child2Values[j] = parent1Values[j];
            }
        }
    }

    private double[] linearRank(int[] ranks)
    {
        double mu = mean(ranks);
        double[] probabilities = new double[ranks.length];
        for(int i=0; i < probabilities.length; i++)
        {
            probabilities[i] = ((2-selectionPressure)/ mu) + ((2*ranks[i]*(selectionPressure-1))/(mu*(mu-1)));
        }
        return probabilities;
    }

    private double[] exponentialRank(int[] ranks)
    {
        double mu = mean(ranks);
        double[] probabilities = new double[ranks.length];
        for(int i=0; i < probabilities.length; i++)
        {
            //probabilities[i] = ((2-selectionPressure)/ mu) + ((2*ranks[i]*(selectionPressure-1))/(mu*(mu-1)));
            probabilities[i] =  (1-Math.exp(-ranks[i]))/(1.0/ranks.length);
        }
        return probabilities;
    }

    private double mean(int[] array)
    {
        double mean = 0.0;
        for(int i=0;i < array.length;i++)
        {
            mean += array[i];
        }
        return (mean/ array.length);
    }

    private Population selectPairs(Population currentPopulation, double[] probabilities)
        {
            ArrayList<Individual> parents = new ArrayList<>(2*numParents);
            for(int i=0; i < 2*numParents; i+=2){
                double rand = Math.random();
                int j;
                for(j=0; j < numParents; j++){
                    if(rand > probabilities[j+1] || j == numParents-2)
                    {
                        parents.add(currentPopulation.getIndividual(j));
                        break;
                    }
                }
                int secondParent = -1;
                while(secondParent == -1 || secondParent == j)
                {
                    rand = Math.random();
                    for(int k = 0; k < numParents; k++)
                    {
                        if(rand > probabilities[k+1] || k == numParents-1)
                        {
                            secondParent = k;
                            break;
                        }
                    }
                }
                parents.add( currentPopulation.getIndividual(secondParent));
            }
            return new Population(parents, currentPopulation.getEvaluation());
        }

    public double getMutateP()
    {
        return pMutate;
    }

    public void setMutate(double p)
    {
        pMutate = p;
    }

}
