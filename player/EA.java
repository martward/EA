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
        Population selection = new Population(numParents, currentPopulation.getEvaluation());

        return null;
    }

    public Population recombine(Population parents){

        return null;
    }

    public Population kill(Population population, Population children){

        return null;
    }

    public Population mutation(Population population){

        return null;
    }
}
