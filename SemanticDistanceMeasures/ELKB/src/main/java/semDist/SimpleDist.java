/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package semDist;

public class SimpleDist implements Distance {

	@Override
	public int getSimilarity(String word1, String word2) {
		if(word1.equals(word2)){
			return 16;
		}
		return 0;
	}

	@Override
	public int getSimilarity(String word1, String word2, String pos) {
		return getSimilarity(word1, word2);
	}

	@Override
	public int getSimilarity(String word1, String pos1, String word2, String pos2) {
		if(word1 == null || word2 == null || pos1 == null || pos2 == null){
			return 0;
		}
		if(word1.equals(word2) && pos1.equals(pos2)){ //2 for perfect match
			return 16;
		}
		else if(word1.equals(word2)){ //1 for match with different pos
			return 8;
		}
		return 0;
	}

}
