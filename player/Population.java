import org.vu.contest.ContestEvaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by sebastien on 4-10-16.
 */
public class Population {


    private final ContestEvaluation evaluation;
    private final int size;
    private ArrayList<Individual> population;

    public Population(ArrayList<Individual> population, ContestEvaluation evaluation)
    {
        this.population = population;
        this.size = population.size();
        this.evaluation = evaluation;
    }

    public Population(int size, ContestEvaluation evaluation)
    {
        this.evaluation = evaluation;
        this.size = size;

        initPopulation();
    }

    private void initPopulation()
    {
        population = new ArrayList<>();
        for (int i = 0; i < size; i++)
        {
            population.add(new Individual());
        }
    }

    public void evaluate()
    {
        for (Individual individual : population)
        {
            double[] array = new double[individual.getParameters().length];
            for(int i=0; i < array.length;i++)
            {
                array[i] = individual.getParameters()[i];
            }
            individual.setFitness((double)evaluation.evaluate(array));
        }

        Collections.sort(population, new Comparator<Individual>() {
            @Override
            public int compare(Individual t1, Individual individual) {
                return Double.compare(individual.getFitness(), t1.getFitness());
            }
        });
    }

    /*
    public void evaluateIndividual(Individual individual){
         individual.setFitness((double)evaluation.evaluate(individual.getParameters()));
    }
    */
    public ContestEvaluation getEvaluation()
    {
        return evaluation;
    }

    public String toString()
    {
        String string = "";
        for (Individual individual : population)
        {
            string += individual.toString() + "\n";
        }
        return string;
    }

    public Population getTopN(int n){
        ArrayList<Individual> top = new ArrayList<>(population.subList(0, n));

        return new Population(top, evaluation);
    }

    public int getPopSize(){
        return population.size();
    }

    public Individual getIndividual(int i){
        return population.get(i);
    }

    public ArrayList<Individual> getPopulation()
    {
        return population;
    }

    public void fitnessSharing(){
        double sigma = 2;
        for(int i=0; i < population.size(); i++)
        {
            double sum = 0;
            for(int j = 0; j < population.size(); j++)
            {
                if(i!=j)
                {
                    sum+=share(i, j, sigma );
                }
            }
            population.get(i).setFitness(population.get(i).getFitness()/sum);
        }
    }

    private double calcDistance(int i, int j)
    {
        double distance = 0.0;
        double[] parameters1 = population.get(i).getParameters();
        double[] parameters2 = population.get(j).getParameters();
        for (int k = 0; k < parameters1.length; k++) {
            distance += Math.pow((parameters1[k] - parameters2[k]), 2);
        }
        return Math.sqrt(distance);
    }

    private double share(int i, int j, double sigma)
    {
        double distance = calcDistance(i,j);
        if( distance <= sigma)
        {
            //System.out.println("jjeeeeeep");
            return 1.0 - (distance/sigma);
        } else
        {
            return 0.0;
        }
    }
}
