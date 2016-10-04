import java.util.Properties;
import org.vu.contest.ContestEvaluation;

// This is an example evalation. It is based on the Fletcher Powell. It is a maximization problem with a maximum of 10 for 
//  	vector a_.
// The Fletcher Powell function itself is for minimization with minimum of f(a_).
// Base performance is calculated as the distance of the expected fitness of a random search (with the same amount of available
//	evaluations) on the F&P function to the function minimum, thus Base = E[f_best_random] - ftarget. Fitness is scaled
//	according to this base, thus Fitness = 10 - 10*(f-fbest)/Base
public class FletcherPowellEvaluation implements ContestEvaluation 
{
	// Evaluations budget
	private final static int EVALS_LIMIT_ = 1000000;
	// The base performance. It is derived by doing  random search on the F&P function (see function method) with the same
	//  amount of evaluations
	private final static double BASE_ = 15796.33;
	// The minimum of the sphere function
	private static double ftarget_;
	
	// Best fitness so far
	private double best_;
	// Evaluations used so far
	private int evaluations_;
	
	// Properties of the evaluation
	private String multimodal_ = "true";
	private String regular_ = "false";
	private String separable_ = "false";
	private String evals_ = Integer.toString(EVALS_LIMIT_);

	// The function's matrices
	//	Matrices as initialized in (Back, 1996)
	private double[][] A_ = {
			{-78, 28, 53, -9, 75, -55, -62, 20, -88, 95, -71, -70, 36, -4, -76, 38, 13, -30, 77, 61, -25, -65, 37, 22, -88, 24, -45, -61, 30, 0},
			{-13, -50, -98, 20, -40, -62, -68, -6, 16, 58, -98, 2, 10, 67, 0, -75, 0, -22, -60, -88, -90, -18, 59, 61, 2, -95, 69, -15, -28, 20}, 
			{27, 73, 63, 81, 15, 92, 0, 59, 63, 62, 97, -22, -49, 66, -93, -43, 71, -97, -81, 74, 37, 80, -51, -93, -85, 99, -30, -8, 76, 14},
			{59, -23, 39, 98, 11, 26, -25, -49, -77, -36, 66, -43, 30, 26, -52, 80, -60, 96, 47, 96, -87, -5, -28, 43, 24, -28, 81, 19, -68, 69}, 
			{-44, -58, -49, -9, -22, 5, -27, 35, 95, -70, 60, 71, -83, 23, 77, 78, 13, 68, -8, 71, 39, -76, -5, 25, 1, 6, -76, -74, -5, 92},
			{0, 81, 30, 1, -52, -32, -56, -70, 31, -53, 75, -12, 41, 14, -51, 10, 22, -84, 10, 17, 46, 90, 99, -15, -37, 89, -67, 64, -55, -23},
			{16, 49, -39, -66, 42, -63, 47, 80, -58, 17, -56, 26, -92, -54, -25, -8, -52, -66, 52, -73, 62, -69, -31, 5, 73, 30, 17, -43, -17, -94},
			{90, -50, -17, -11, 69, 92, -49, 2, -2, 35, 64, -38, -6, 92, -98, 20, 31, -80, 95, 52, 91, 76, -67, 53, 32, 76 , -83, 47, -74, 66},
			{-6, 2, -32, 1, 51, -17, 2, 86, -12, -77, 95, 37, -68, 23, -57, 99, -97, -98, 69, 94, 40, 62, 25, 66, 27, 39, 24, -37, -5, -97},
			{65, -7, 19, 95, 9, -44, 10, 96, -85, 13, -13, -17, -25, 95, 37, 68, -40, -76, 31, -84, 69, -45, -62, -38, 60, 24, -35, -2, 83, 0},
			{-22, -97, -89, 81, 4, 45, -6, -6, 52, 68, -34, 21, 47, 55, 52, 92, 92, 65, -56, -90, -93, -25, -53, 21, -10, 38, -90, -69, -74, -78},
			{54, 59, 61, -54, -74, 51, 67, 77, -58, -61, 29, 36, -1, -52, 52, -31, -34, -61, -60, -52, 47, -77, -88, 70, 89, -26, 66, 28, 7, 57},
			{50, -42, -71, -4, 73, 66, -93, -82, 15,13, -95, -86, 4, -48, -49, -10, 41, -41, 10, 2, -4, -73, -18, -51, 21,69 ,12, -91, -30, -5},
			{-21, 11, -45, -76, 68, 96, -63, 15, 70, -35, 39, -1, -94, 44, -91, 27, 32, 20, -9, 11, -77, -43, -63, 77, 98, 67, -81, 37, -81, -6},
			{-67, 6, 74, -2, -42, 36, -92, 84, 91, -43, -23, -3, 78, -4, -32, -48, 30 ,38, -29, -7, -20, 64, 51, 6, 56, -73, 22, 86, -67, -43},
			{-30, -68, 92, 2, 76, -22, -72, -97, 86, 50, -11, 80, -82, 54, 26, 68, -94, 90, -8, -48, -87, 20, 80, 84, 70, -78, 95, -23, -30, 60},
			{-67, -45, 68, -20, 80, -25, -26, -21, -35, 60, 86, 99, -17, 30, -55, -77, 50, -91, 31, -49, -52, -42, -70, -2, -65, -7, -43, 45, -53, 89},
			{-50, 8, -31, -62, -7, -5, -60, -26, -91, 42, 46, 90, -37, -49, -2, -14, 50, -56, 93, -43, -98, -17, -69, -76, -61, 47, 36, -76, -20, 87},
			{-99, 21, 29, -59, -28, 2, -80, 38, -98, -41, 4, -49, 0, -52, 74, -29, 99, 53, -4, 56, -87, 43, -44, -30, -7, -97, -39, -93, 18, -5},
			{97, 96,12, -62, 89, 34, 68, -76, -24, 0, -95, -85, -2, 54, -34, 94, 20, 17, 21, 71, 13, 4, 10, 98, 29, 16, -88, -47, 22, 22}, 
			{-76, 29, -76, 21, 87, -41, 64, -40, -76, -93, -58, 5, 87, -31, 48, -57, 78, -56, -17, 93, -93, -69, 1, -4, -15, -95, 56, 50, -30, -64}, 
			{-26, 34, 72, 75, -86, 30, -17, 4, -28, -69, 54, -92, -89, 2, 65, -93, 71, -36, -66, 38, -65, 57, -51, 55, -96, -83, 84, -37, -73, 21}, 
			{2, -69, 99, -64, -10, -95, -29, 18, 89, -17, 11, -14, -96, 62, -9, -53, 4, 67, -45, 59, -13, -8, -88, 85, 64, 91, 43, 55, -99, -96},
			{-44, 15, 12, -2, 54, -61, 52, -36, -27, -61, 32, 60, -88, 54, -47, -10, -93, -42, -49, -23, 90, -33, -40, -43, 56, 48, 74, -50, 26, 52}, 
			{-69, 92, -77, 27, -14, -91, -74, 80, -66, -33, 30, -44, 69, -87, -53, 3, 31, -61, -66, 84, 90, 99, 6, -93, -74, -54, -78, 4, -77, -53},
			{93, 44, -14, 81, 78, 75, -60, -2, 54, 66, 60, -54, 86, 77, -43, -22, -51, 3, 5, -6, 1, 62, -68, -78, 42, 47, 34, -30, 88, -31}, 
			{5, 70, 11, -26, -30, 28, 76, 91, -10, -89, 90, 27, 42, 49, -95, 63, 83, 45, -90, -7, 42, -72, 13, 92, 89, -92, -31, 64, 64, 82},
			{29, 18, 88, -66, -20, -54, 59, -23, 7, -18, -30, -15, 82, -60, -59, -6, -35, 11,71, -19, 93, 59, -67, 45, -33, 76, -67, -23, -25, -26},
			{76, -48, 42, 27, -15, 24, -44, -3, 12, 80, 26, 14, -35, 97, -71, 26, -9, -54, -36, 87, -87, 14, -6, -87, 89, -14, -12, 62, 39, 93}, 
			{53, -39, 38, -54, 84,56, -51, -37, 98, -18, -3, 69, 50, -3, -38, -20, 48, -33, -40, 83,38, -30, -95, 0, 38, 57, 34, -63, -46, -57}
	};
	private double[][] B_ = {
			{97, -25, -78, -27, 85, 0, -55, 68, 61, -57, 88, 2, -9, -44, -52, -11, -72, 10, -33, -19, 56, 68, -55, -54, -10, 1, -65, 14, -19, 64}, 
			{30, 25, -32, -1, 15, -5, 28, 92, 99, 42, -83, 21, 10, 82, -45, 76, 75, 46, 58, 74, 75, 12, -86, -11, 0, -29, -17, 15, -58, 59},
			{87, -31, -92, -47, 25, -68, 76, -43, -87, 92, -58, -25, -88, -45, 89, 82, -40, 85, 96, 78, 43, -61, -99, -22, -57, -37, 65, -26, -65, -20}, 
			{31, -37, 6, 27, -1, 3, 76, -70, 73, -15, 0, -97, 71, -66, 7, -43, 63, -62, -44, -82, -62, 38, -39, 88, 60, 67, 90, 97, -96, 51}, 
			{13, -19, 85, -59, -18, 63, -62, 39, 77, 5, 6, -38, -88, 3, -63, 0, -6, 66, -32, -91, -92, -56, -24, 48, -34, -10, 95, 57, 91, 64}, 
			{64, 82, -5, 5, -25, -69, 70, 21, 16, 27, 40, -37, -31, 45, -28, 35, 58, 23, 50, 3, 83, -41, 90, -63, 36, -51, 6, -66, 95, -15}, 
			{41, -61, 60, 69, -16, 24, 22, 43, 2, 54, 5, 54, 74, -88, -99, -26, 12, 85, 45, 88, -64, 79, 8, -44, 9, -96, -95, -82, 2, 56}, 
			{38, 81, 5, 30, -45, 0, 2, -72, -17, 53, -95, -93, 22, 38, -4, 50, -39, -30, 43, -65, -33, -72, -99, -78, 45, -82, -22, 40, 68, -41}, 
			{-57, 77, 83, 2, -61, 27, -3, -3, -76, 54, 86, 73, -25, -65, 41, 50, -33, -42, 3, -40, 96, -3, 92, 67, 37, 68, -45, 0, 99, 13},
			{13, 31, 78, -12, 17, -69, 67, -70, 26, -23, 29, 90, 40, -69, 53, 10, 32, 86, 81, -17, -26, -61, -85, -75, 70, -12, 35, 22, -64, -28}, 
			{72, -18, -52, -66, -73, -85, -23, -50, 64, 70, 17, 38, 5, 87, -44, -43, -37, 51, -40, -15, 80, 93, 60, -52, 13, 0, -63, 98, -42, 39}, 
			{86, -84, -3, 49, 54, -27, 93, 42, -38, 37, 84, -7, -33, 20, -59, 41, -98, -49, -23, -89, -2, -44, 25, 95, 19, -49, -43, 54, -3, -3}, 
			{95, 8, 3, -21, 20, -10, -46, -91, 72, 53, -56, 1, 15, -36, 20, 93, 29, 32, 7, 67, -89, -64, 93, 88, -8, -4, 9, -78, -96, 30}, 
			{-25, 1, 41, -7, 28, 60, 1, -82, 33, -47, 79, -42, 10, 63, 99, 81, 6, 89, 11, -50, 26, 51, 47, 80, 14, -96, -47, -98, 78, -43}, 
			{9, -39, -93, -90, 4, 23, 31, -29, -9, 93, 0, -86, -61, -27, -86, -64, -96, -61, 43, -76, 10, 65, -89, -85, 95, -6, 96, 2, 56, -38}, 
			{94, -98, 25, 40, -88, -77, -84, -58, -75, -29, -88, -42, 95, 28, 2, 31, -50, 66, 79, 47, -55, -60, -6, -30, 36, 97, -43, -59, -39, -46}, 
			{1, 18, -74, 44, -50, -34, -56, -49, 27, -73, -87, -51, -91, 40, -9, 31, -78, 66, -63, 25, 69, -18, 46, 87, 69, -70, 20, 40, 73, 72}, 
			{95, -33, -42, -62, -20, -18, -33, -33, -73, 17, 65, 27, -27, 63, 0, 60, 5, 42, 47, 75, -12, 90, -68, 10, 33, -34, 7, -70, -43, 74}, 
			{86, 33, 3, -23, 17, -75, 14, -40, -48, 34, 31, -51, -77, -79, -98, 94, -44, 30, -47, -28, -28, -79, 56, -47, -32, -33, -93, 14, 28, 83}, 
			{80, -4, -28, -77, 35, -74, -62, -76, -78, -72, 84, -96, -12, -63, 40, 62, -67, -35, 22, -72, 9, 14, -57, -71, 43, -55, 55, 9, -18, -93},
			{61, 89, -28, 72, 26, 30, 74, -34, -7, 17, 34, 67, -23, 98, -88, -14, -6, 36, -55, -38, 14, 28, -42, -69, 46, 54, -72, 9, 10, -13},
			{-48, 18, -73, 32, -9, -52, -62, 82, -5, -34, -28, 40, 41, -66, -87, -73, 15, -19, -3, 85, -16, 19, 35, 94, -20, -72, -54, 26, 27, -85}, 
			{-82, 37, -18, 86, 30, -65, -65, -18, 89, -13, -59, 0, 23, -80, -55, -48, -73, 70, -2, -51, -10, -96, -80, 39, -2, 41, 68, -61, -68, 20},
			{-94, 80, -52, 50, 38, 1, 4, 50, -34, 19, -41, -8, -9, -94, 9, -82, 23, -14, 45, 41, 57, 69, 7, 67, 25, -40, -22, 53, 93, 0}, 
			{-83, -91, 2, -43, 90, -8, -44, -42, 82, 71, 34, -54, 48, -85, -42, -28, 6, -19, 18, -14, 0, -58, -43, 3, 59, -56, 78, 52, -72, -63}, 
			{5, 23, 84, 46, -65, 47, -37, -95, 28, -59, 10, 13, 85, -48, -95, -98, 9, -67, -19, 29, 35, 62, 73, 30, 51, -21, 22, -53, 90, 47}, 
			{-52, -42, -81, 7, -85, 1, -30, 75, 95, 24, -13, -27, 15, 99, 79, 47, -1, 89, 61, 29, 96, -38, -28, -26, -14, -8, -39, 18, -79, 91}, 
			{11, -53, -82, 77, 96, -72, 58, -82, -93, 26, -63, -72, -29, 56, 84, 60, 97, -84, -89, -63, 14, -59, -50, 10, -8, 18, 97, 52, 90, 81}, 
			{-47, 67, -53, -32, -5, 45, -86, -43, 51, -87, -82, 46, 64, -45, 3, -35, 59, 55, 36, -43, -83, 49, -3, -83, 57, 71, 62, 68, 3, -83},
			{80, 67, -14, 42, -70, -10, -66, 73, -7, 92, 93, 93, 78, -62, -74, -51, 16, 11, -12, 15, -95, -46, 13, -74, -82, -38, 19, -75, -39, 33}	
	};
	private double[] a_ = {0.435934, 0.550595, -1.283410, -0.0734284,  -2.6051900, 2.104100, 1.867540, -3.012750, 0.8628350,  0.0666833, 2.336110, -0.658149, -3.112430, -3.0755600, 0.8418540, -0.692549, 3.062840, -0.917399, 0.2111350, -1.4526100, 2.482440, 2.008340, 0.906190, -0.1087510, 0.6348730, 1.458810, 1.240920, 2.303110, -2.3116000, -2.1476100};
	

	public FletcherPowellEvaluation()
	{
		best_ = 0;
		evaluations_ = 0;	

		// Calculate the target objective value
		ftarget_ = function(a_);
	}

	// The Fletcher Powell function. The global minumum is located at a_
	private double function(double[] x)
	{	
		double Ai, Bi, sum;
		
		int dim = 10;
		sum = 0;
		for(int i=0; i<dim; i++){
			Ai = 0;
			Bi = 0;
			for(int j=0; j<dim; j++){
				Ai += A_[i][j]*Math.sin(a_[j]) + B_[i][j]*Math.cos(a_[j]);
				Bi += A_[i][j]*Math.sin(x[j]) + B_[i][j]*Math.cos(x[j]);
			}
			sum += Math.pow(Ai-Bi, 2);
		}	
		return sum;
	}
	
	@Override
	public Object evaluate(Object result) 
	{
		// Check argument
		if(!(result instanceof double[])) throw new IllegalArgumentException();
		double ind[] = (double[]) result;
		if(ind.length!=10) throw new IllegalArgumentException();
		
		if(evaluations_>EVALS_LIMIT_) return null;
		
		// Transform function value (F&P is minimization).
		// Normalize using the base performance
		double f = 10 - 10*( (function(ind)-ftarget_) / BASE_ ) ;
		if(f>best_) best_ = f;
		evaluations_++;
		
		return new Double(f);
	}

	@Override
	public Object getData(Object arg0) 
	{
		return null;
	}

	@Override
	public double getFinalResult() 
	{
		return best_;
	}

	@Override
	public Properties getProperties() 
	{
		Properties props = new Properties();
		props.put("Multimodal", multimodal_);
		props.put("Regular", regular_);
		props.put("Separable", separable_);
		props.put("Evaluations", evals_);
		return props;
	}
}
