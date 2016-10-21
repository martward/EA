javac -cp contest.jar player36.java Individual.java EA.java Population.java 
jar cmf MainClass.txt submission.jar player36.class Individual.class EA.class Population.class EA\$MUTATION_TYPE.class  Population\$1.class EA\$SELECTION_TYPES.class EA\$RECOMBINATION_TYPES.class EA\$1.class EA\$KILL_TYPE.class EA\$mated.class
java -jar testrun.jar -submission=player36 -evaluation=FletcherPowellEvaluation -seed=1 

