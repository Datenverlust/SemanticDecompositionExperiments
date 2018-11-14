/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package semDist;

public interface Distance {

	int getSimilarity(String word1, String word2);
	int getSimilarity(String word1, String word2, String pos);
	int getSimilarity(String word1, String pos1, String word2, String pos2);
	
}
