javac -cp contest.jar player36.java Individual.java EA.java Population.java 
jar cmf MainClass.txt submission.jar player36.class Individual.class EA.class Population.class Population\$1.class EA\$SELECTION_TYPES.class EA\$RECOMBINATION_TYPES.class EA\$1.class
java -jar testrun.jar -submission=player36 -evaluation=SphereEvaluation -seed=1 
