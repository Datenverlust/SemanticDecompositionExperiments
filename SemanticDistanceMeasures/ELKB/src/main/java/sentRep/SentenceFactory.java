/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package sentRep;

/**
 * Defines functions which are used by a Sentence Factory to create a Sentence Object.
 * 
 * @author Alistair Kennedy
 * @version 1.2 Dec 2008 
 */
public interface SentenceFactory {
	
	enum Resource { ROGETS, WORDNET, SIMPLE }
	
	/**
	 * This builds a hashtables of features and values out of an array of words.  In
	 * the implementation we used, these words contained POS tags assigned by the 
	 * Brill tagger.
	 * 
	 * @param sWords
	 * @return Sentence
	 */
	Sentence buildRepresentationVector(String[] sWords);
	
	/**
	 * This builds a hashtables of features and values out of an array of words.  In
	 * the implementation we used, these words contained POS tags assigned by the 
	 * Brill tagger or some other tagger.  A maximum phrase length is defined.
	 * 
	 * @param sWords
	 * @param maxPhraseLength
	 * @return Sentence
	 */
	Sentence buildRepresentationVector(String[] sWords, int maxPhraseLength);
	

	/**
	 * This builds a hashtables of features and values out of an array of words.  In
	 * the implementation we used, these words contained POS tags assigned by the 
	 * Brill tagger or some other tagger.  A maximum phrase length is defined as is the mean
	 * 
	 * @param sWords
	 * @param maxPhraseLength
	 * @param mean
	 * @return Sentence
	 */
	Sentence buildRepresentationVector(String[] sWords, int maxPhraseLength, double mean);
	
}
