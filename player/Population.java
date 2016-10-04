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
            individual.setFitness((double)evaluation.evaluate(individual.getParameters()));
        }
        Collections.sort(population, new Comparator<Individual>() {
            @Override
            public int compare(Individual individual, Individual t1) {
                return Double.compare(individual.getFitness(), t1.getFitness());
            }
        });
    }

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
}
