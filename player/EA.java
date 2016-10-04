import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by sebastien on 4-10-16.
 */
public class EA {

    private final SELECTION_TYPES selectionType;
    private final double selectionPressure;
    private final int numParents;

    public enum SELECTION_TYPES {
        UNIFORM, TOURNAMENT, ROULETTE, STOCHASTIC
    }

    public EA(SELECTION_TYPES selectionType, double selectionPressure, int numParents){
        this.selectionType = selectionType;
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

        return null;
    }

    public Population mutation(Population population){

        return null;
    }
}
