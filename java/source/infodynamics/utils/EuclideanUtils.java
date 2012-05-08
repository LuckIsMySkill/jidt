package infodynamics.utils;

/**
 * 
 * A set of utilities for manipulating vectors in Euclidean space.
 * 
 * @author Joseph Lizier
 *
 */
public class EuclideanUtils {

	public static int MAX_TIMESTEPS_FOR_FAST_DISTANCE = 2000;
	
	public static final int NORM_EUCLIDEAN = 0;
	public static final String NORM_EUCLIDEAN_STRING = "EUCLIDEAN";
	public static final int NORM_EUCLIDEAN_NORMALISED = 1;
	public static final String NORM_EUCLIDEAN_NORMALISED_STRING = "EUCLIDEAN_NORMALISED";
	public static final int NORM_MAX_NORM = 2;
	public static final String NORM_MAX_NORM_STRING = "MAX_NORM";
	private static int normToUse = 0;

	public EuclideanUtils() {
		super();
	}

	public static double[] computeMinEuclideanDistances(double[][] observations) {
		if (observations.length <= MAX_TIMESTEPS_FOR_FAST_DISTANCE) {
			return EuclideanUtils.computeMinEuclideanDistancesFast(observations);
		} else {
			return computeMinEuclideanDistancesNaive(observations);
		}
	}
	
	
	/**
	 * Naive method for computing minimum distance - slower but needs less memory.
	 * Made public for debugging only. O(d.n^2) speed
	 * 
	 * @param observations
	 * @return
	 */
	public static double[] computeMinEuclideanDistancesNaive(double[][] observations) {
		int numObservations = observations.length;
		int dimensions = observations[0].length;
		double[] distances = new double[numObservations];
		for (int t = 0; t < numObservations; t++) {
			double minDistance = Double.POSITIVE_INFINITY;
			for (int t2 = 0; t2 < numObservations; t2++) {
				if (t == t2) {
					continue;
				}
				double thisDistance = 0.0;
				for (int d = 0; (d < dimensions) && (thisDistance < minDistance); d++) {
					double distanceOnThisVar = (observations[t][d] - observations[t2][d]);
					thisDistance += distanceOnThisVar * distanceOnThisVar;
				}
				// Now we need to sqrt the distance sum
				thisDistance = Math.sqrt(thisDistance);
				// Now check if this is a lower distance
				if (thisDistance < minDistance) {
					minDistance = thisDistance;
				}
			}
			distances[t] = minDistance;
		}
		return distances;
	}

	/**
	 * Return the minimum Euclidean distance from each point to any other observation.
	 * Computes this faster than using naive computation.
	 * 
	 * Exposed as a public method for debugging purposes only.
	 * 
	 * @param observations
	 * @return
	 */
	public static double[] computeMinEuclideanDistancesFast(double[][] observations) {
		
		int dimensions  = observations[0].length;
		
		int timeSteps = observations.length;
		// Hold the sqr distance from index1 to index2 ...
		double[][] sqrDistance = new double[timeSteps][timeSteps];
		// ... computed over this many of the variables so far
		int[][] addedInUpToVariable = new int[timeSteps][timeSteps];
		double[] minDistance = new double[timeSteps];
		
		for (int t1 = 0; t1 < timeSteps; t1++) {
			// Current minimum distance from this index to another point:
			double minSqrDistance = Double.POSITIVE_INFINITY;

			// First grab the minimum distance from nodes for which the distance might
			//  have already been measured
			for (int t2 = 0; t2 < t1; t2++) {
				if (addedInUpToVariable[t2][t1] == dimensions) {
					// We have previously computed this distance from t2 to t1
					sqrDistance[t1][t2] = sqrDistance[t2][t1];
					// unnecessary, since we won't be looking at [t1][t2] later:
					addedInUpToVariable[t1][t2] = dimensions;
					if (sqrDistance[t1][t2] < minSqrDistance) {
						minSqrDistance = sqrDistance[t1][t2];
					}
				}
			}
			// Now check the previously considered source nodes which didn't have their full distance
			//  computed in case we need to compute them
			for (int t2 = 0; t2 < t1; t2++) {
				if (addedInUpToVariable[t2][t1] != dimensions) {
					// We have not finished computing this distance from t1
					addedInUpToVariable[t1][t2] = addedInUpToVariable[t2][t1];
					sqrDistance[t1][t2] = sqrDistance[t2][t1];
					for (; (sqrDistance[t1][t2] < minSqrDistance) &&
							(addedInUpToVariable[t1][t2] < dimensions);
							addedInUpToVariable[t1][t2]++) {
						double distOnThisVar = observations[t1][addedInUpToVariable[t1][t2]] -
												observations[t2][addedInUpToVariable[t1][t2]]; 
						sqrDistance[t1][t2] += distOnThisVar * distOnThisVar;
					}
					if (sqrDistance[t1][t2] < minSqrDistance) {
						// we finished the calculation and t2 is now the closest observation to t1
						minSqrDistance = sqrDistance[t1][t2];
					}
				}
			}
			// Now check any source nodes t2 for which there is no chance we've looked at the
			//  the distance back to t1 yet
			for (int t2 = t1 + 1; t2 < timeSteps; t2++) {
				for (; (sqrDistance[t1][t2] < minSqrDistance) &&
						(addedInUpToVariable[t1][t2] < dimensions);
						addedInUpToVariable[t1][t2]++) {
					double distOnThisVar = observations[t1][addedInUpToVariable[t1][t2]] -
											observations[t2][addedInUpToVariable[t1][t2]]; 
					sqrDistance[t1][t2] += distOnThisVar * distOnThisVar;
				}
				if (sqrDistance[t1][t2] < minSqrDistance) {
					// we finished the calculation and  t2 is now the closest observation to t1
					minSqrDistance = sqrDistance[t1][t2];
				}
			}
			
			minDistance[t1] = Math.sqrt(minSqrDistance);
		}
		return minDistance;
	}

	public static double maxJointSpaceNorm(double[] x1, double[] y1,
			   double[] x2, double[] y2) {
		return Math.max(norm(x1, x2), norm(y1,y2));
	}

	/**
	 * Computing the configured norm between vectors x1 and x2.
	 * 
	 * @param x1
	 * @param x2
	 * @return
	 */
	public static double norm(double[] x1, double[] x2) {
		switch (normToUse) {
		case NORM_EUCLIDEAN_NORMALISED:
			return euclideanNorm(x1, x2) / Math.sqrt(x1.length);
		case NORM_MAX_NORM:
			return maxNorm(x1, x2);
		case NORM_EUCLIDEAN:
		default:
			return euclideanNorm(x1, x2);
		}
	}
	
	/**
	 * Computing the norm as the Euclidean norm.
	 * 
	 * @param x1
	 * @param x2
	 * @return
	 */
	public static double euclideanNorm(double[] x1, double[] x2) {
		double distance = 0.0;
		for (int d = 0; d < x1.length; d++) {
			double difference = x1[d] - x2[d];
			distance += difference * difference;
		}
		return Math.sqrt(distance);
	}

	/**
	 * Computing the norm as the Max norm.
	 * 
	 * @param x1
	 * @param x2
	 * @return
	 */
	public static double maxNorm(double[] x1, double[] x2) {
		double distance = 0.0;
		for (int d = 0; d < x1.length; d++) {
			double difference = x1[d] - x2[d];
			// Take the abs
			if (difference < 0) {
				difference = -difference;
			}
			if (difference > distance) {
				distance = difference;
			}
		}
		return distance;
	}

	/**
	 * Compute the x and y norms of all other points from
	 *  the data points at time step t.
	 * Puts norms of t from itself as infinity, which is useful
	 *  when counting the number of points closer than epsilon say.
	 * 
	 * @param mvTimeSeries1
	 * @param mvTimeSeries2
	 * @return
	 */
	public static double[][] computeNorms(double[][] mvTimeSeries1,
			double[][] mvTimeSeries2, int t) {
		
		int timeSteps = mvTimeSeries1.length;
		double[][] norms = new double[timeSteps][2];
		for (int t2 = 0; t2 < timeSteps; t2++) {
			if (t2 == t) {
				norms[t2][0] = Double.POSITIVE_INFINITY;
				norms[t2][1] = Double.POSITIVE_INFINITY;
				continue;
			}
			// Compute norm in first direction
			norms[t2][0] = norm(mvTimeSeries1[t], mvTimeSeries1[t2]);
			// Compute norm in second direction
			norms[t2][1] = norm(mvTimeSeries2[t], mvTimeSeries2[t2]);
		}
		return norms;
	}
	
	/**
	 * Compute the x, y and z norms of all other points from
	 *  the data points at time step t.
	 * Puts norms of t from itself as infinity, which is useful
	 *  when counting the number of points closer than epsilon say.
	 * 
	 * @param mvTimeSeries1
	 * @param mvTimeSeries2
	 * @param mvTimeSeries3
	 * @return
	 */
	public static double[][] computeNorms(double[][] mvTimeSeries1,
			double[][] mvTimeSeries2, double[][] mvTimeSeries3, int t) {
		
		int timeSteps = mvTimeSeries1.length;
		double[][] norms = new double[timeSteps][3];
		for (int t2 = 0; t2 < timeSteps; t2++) {
			if (t2 == t) {
				norms[t2][0] = Double.POSITIVE_INFINITY;
				norms[t2][1] = Double.POSITIVE_INFINITY;
				norms[t2][2] = Double.POSITIVE_INFINITY;
				continue;
			}
			// Compute norm in first direction
			norms[t2][0] = norm(mvTimeSeries1[t], mvTimeSeries1[t2]);
			// Compute norm in second direction
			norms[t2][1] = norm(mvTimeSeries2[t], mvTimeSeries2[t2]);
			// Compute norm in third direction
			norms[t2][2] = norm(mvTimeSeries3[t], mvTimeSeries3[t2]);
		}
		return norms;
	}

	/**
	 * Compute the norms for each marginal variable for all other points from
	 *  the data points at time step t.
	 * Puts norms of t from itself as infinity, which is useful
	 *  when counting the number of points closer than epsilon say.
	 * 
	 * @param mvTimeSeries
	 * @return
	 */
	public static double[][] computeNorms(double[][] mvTimeSeries, int t) {
		
		int timeSteps = mvTimeSeries.length;
		int variables = mvTimeSeries[0].length;
		
		double[][] norms = new double[timeSteps][variables];
		for (int t2 = 0; t2 < timeSteps; t2++) {
			if (t2 == t) {
				for (int v = 0; v < variables; v++) {
					norms[t2][v] = Double.POSITIVE_INFINITY;
				}
				continue;
			}
			for (int v = 0; v < variables; v++) {
				norms[t2][v] = Math.abs(mvTimeSeries[t][v] - mvTimeSeries[t2][v]);
			}
		}
		return norms;
	}

	/**
	 * Sets which type of norm will be used by calls to norm()
	 * 
	 * @param normType
	 */
	public static void setNormToUse(int normType) {
		normToUse = normType;
	}

	/**
	 * Sets which type of norm will be used by calls to norm()
	 * 
	 * @param normType
	 */
	public static void setNormToUse(String normType) {
		if (normType.equalsIgnoreCase(NORM_EUCLIDEAN_NORMALISED_STRING)) {
			normToUse = NORM_EUCLIDEAN_NORMALISED;
		} else if (normType.equalsIgnoreCase(NORM_MAX_NORM_STRING)) {
			normToUse = NORM_MAX_NORM;
		} else {
			normToUse = NORM_EUCLIDEAN;
		}
	}
	
	public static String getNormInUse() {
		switch (normToUse) {
		case NORM_EUCLIDEAN_NORMALISED:
			return NORM_EUCLIDEAN_NORMALISED_STRING;
		case NORM_MAX_NORM:
			return NORM_MAX_NORM_STRING;
		default:
		case NORM_EUCLIDEAN:
			return NORM_EUCLIDEAN_STRING;
		}
	}
}
