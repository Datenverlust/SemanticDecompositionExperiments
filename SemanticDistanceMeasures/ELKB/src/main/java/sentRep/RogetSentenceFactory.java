/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package sentRep;

import java.util.*;

import ca.site.elkb.*;

/**
 * Used to generate a sentence with Roget's concepts added to it.
 * 
 * @author Alistair Kennedy 
 * @version 1.3 Nov 2009 
 */

public class RogetSentenceFactory implements SentenceFactory{
	public RogetELKB elkb;
	//private int year;

	/**
	 * Builds a Roget's Sentence Representation maker
	 * 
	 * @param year
	 */	
	public RogetSentenceFactory(String year) {
		// Initialize Roget
		elkb = new RogetELKB(year);
		//this.year = year;
	}

	/**
	 * Builds a Roget's Sentence Representation maker
	 * 
	 */		
	public RogetSentenceFactory() {
		// Initialize Roget
		elkb = new RogetELKB();
		//this.year = 1911;
	}
	
	private double normalDist(double x, double ave, double var){
		return (1.0/(Math.sqrt(2*Math.PI)*Math.sqrt(var)))*Math.pow(Math.E, (-Math.pow(x-ave, 2)/(2*var)));
	}
	
	/**
	 * This finds all the words in sWords that appear in Roget's Thesaurus and
	 * represents them as weighted head groups, heads, pos's and paragraphs.
	 * Words not found in Roget's will be included, but without any representation
	 * from Roget's Thesuaurs.
	 * 
	 * @param sWords
	 * @return Sentence
	 */
	public Sentence buildRepresentationVector(String[] sWords){
		return buildRepresentationVector(sWords, sWords.length);
	}
	

	/**
	 * This finds all the words in sWords that appear in Roget's Thesaurus and
	 * represents them as weighted head groups, heads, pos's and paragraphs.
	 * Words not found in Roget's will be included, but without any representation
	 * from Roget's Thesuaurs.  Max phrase length is defined by caller.
	 * 
	 * @param sWords
	 * @param maxPhraseLength
	 * @return Sentence
	 */
	public Sentence buildRepresentationVector(String[] sWords, int maxPhraseLength){
		return buildRepresentationVector(sWords, maxPhraseLength, 4.0); //4.0 best mean for Roget 1911.
	}
	
	/**
	 * This finds all the words in sWords that appear in Roget's Thesaurus and
	 * represents them as weighted head groups, heads, pos's and paragraphs.
	 * Words not found in Roget's will be included, but without any representation
	 * from Roget's Thesuaurs.  Max phrase length is defined by caller as is the 
	 * mean.
	 * 
	 * @param sWords
	 * @param maxPhraseLength
	 * @param mean
	 * @return Sentence
	 */
	public Sentence buildRepresentationVector(String[] sWords, int maxPhraseLength, double mean){
		//double mean = 4.0;//4.0 for 1987 5.0 for 1911
		//if(year == 1911){
		//	mean = 5.0;
		//}
		double var = 1.0;
		int max = Math.min(sWords.length, maxPhraseLength);
		Hashtable<String, Pair<Double, Double>> sen = new Hashtable<String, Pair<Double, Double>>();
		if(maxPhraseLength < 0){
			max = sWords.length;
		}
		for(int n = max; n > 1; n--){
			for(int i = 0; i <= sWords.length-n; i++){ //for each n-gram of that length
				String[] parts = sWords[i].split("/");
				parts[0] = parts[0].toLowerCase();
				String nGram = parts[0];
				for(int k = i+1; k < i+n; k++){ //for every word in the n-gram
					String[] parts2 = sWords[k].split("/");
					parts2[0] = parts2[0].toLowerCase();
					nGram += " " + parts2[0];
				}
				ArrayList<int[]> list = elkb.index.getEntryListNumerical(nGram);
				for(int j = 0; j < list.size(); j++){ // use instances
					int[] location = list.get(j);
					String currentVal = "";
					for(int k = 0; k <= 8; k++){
						currentVal += location[k];
						if(sen.containsKey(currentVal)){
							Pair<Double, Double> oldVals = sen.get(currentVal);
							double oldVal1 = oldVals.getModified();
							double oldVal2 = oldVals.getOriginal();
							double newVal1 = oldVal1+((double)n*normalDist(k+1, mean, var)/(double)list.size());
							double newVal2 = oldVal2+((double)n/(double)list.size());
							Pair<Double, Double> p = new Pair<Double, Double>();
							p.setOriginal(newVal2);
							p.setModified(newVal1);
							sen.put(currentVal, p);
						}
						else{
							double newVal1 = ((double)n*normalDist(k+1, mean, var)/(double)list.size());
							double newVal2 = ((double)n/(double)list.size());
							Pair<Double, Double> p = new Pair<Double, Double>();
							p.setOriginal(newVal2);
							p.setModified(newVal1);
							sen.put(currentVal, p);
						}
						currentVal += ".";
					}
				}
				if(list.size() > 0){
					for(int rep = i; rep < i+n; rep++){
						sWords[rep] = "*";
					}
				}
			}
		}
		
		//for individual words
		
		for(int i = 0; i < sWords.length; i++){
			String[] parts = sWords[i].split("/");
			parts[0] = parts[0].toLowerCase();
			if(parts.length == 2 && (parts[1].contains("NN") || parts[1].contains("VB") || parts[1].contains("JJ") || parts[1].contains("RB"))){ 
				ArrayList<int[]> list = elkb.index.getEntryListNumerical(parts[0]);
				if(list.size() == 0){ // add words not found in thesaurus
					if(sen.containsKey(parts[0])){
						Pair<Double, Double> oldVals = sen.get(parts[0]);
						double oldVal1 = oldVals.getModified();
						double oldVal2 = oldVals.getOriginal();
						double newVal1 = oldVal1+(1.0*normalDist(1, mean, var));
						double newVal2 = oldVal2+(1.0);
						Pair<Double, Double> p = new Pair<Double, Double>();
						p.setOriginal(newVal2);
						p.setModified(newVal1);
						//sen.put(parts[0], p);
						sen.put(sWords[i], p);
					}
					else{
						double newVal1 = (1.0*normalDist(1, mean, var));
						double newVal2 = (1.0);
						Pair<Double, Double> p = new Pair<Double, Double>();
						p.setOriginal(newVal2);
						p.setModified(newVal1);
						//sen.put(parts[0], p);
						sen.put(sWords[i], p);
					}
				}
				double count = 0;
				for(int j = 0; j < list.size(); j++){ // count instances
					int[] location = list.get(j);
					if((parts[1].contains("NN") && elkb.index.convertToPOS(""+location[5]).equals("N."))
							|| (parts[1].contains("VB") && elkb.index.convertToPOS(""+location[5]).equals("VB."))
							|| (parts[1].contains("JJ") && elkb.index.convertToPOS(""+location[5]).equals("ADJ."))
							|| (parts[1].contains("RB") && elkb.index.convertToPOS(""+location[5]).equals("ADV.")))
					{
						count++;
					}
				}
				for(int j = 0; j < list.size(); j++){ // use instances
					int[] location = list.get(j);
					if((parts[1].contains("NN") && elkb.index.convertToPOS(""+location[5]).equals("N."))
							|| (parts[1].contains("VB") && elkb.index.convertToPOS(""+location[5]).equals("VB."))
							|| (parts[1].contains("JJ") && elkb.index.convertToPOS(""+location[5]).equals("ADJ."))
							|| (parts[1].contains("RB") && elkb.index.convertToPOS(""+location[5]).equals("ADV.")))
					{
						String currentVal = "";
						for(int k = 0; k <= 8; k++){
							if(k != 0){
								currentVal += ".";
							}
							currentVal += location[k];
							if(sen.containsKey(currentVal)){
								Pair<Double, Double> oldVals = sen.get(currentVal);
								double oldVal1 = oldVals.getModified();
								double oldVal2 = oldVals.getOriginal();
								double newVal1 = oldVal1+(1.0*normalDist((8-k)+1, mean, var)/count);
								double newVal2 = oldVal2+(1.0/count);
								Pair<Double, Double> p = new Pair<Double, Double>();
								p.setOriginal(newVal2);
								p.setModified(newVal1);
								sen.put(currentVal, p);
							}
							else{
								double newVal1 = (1.0*normalDist((8-k)+1, mean, var)/count);
								double newVal2 = (1.0/count);
								Pair<Double, Double> p = new Pair<Double, Double>();
								p.setOriginal(newVal2);
								p.setModified(newVal1);
								sen.put(currentVal, p);
							}
						}
					}
				}
			}
			else if(parts.length != 2){
				
			}
			//else{ // add words not found in thesaurus (different POS)
				if(sen.containsKey(parts[0])){
					Pair<Double, Double> oldVals = sen.get(parts[0]);
					double oldVal1 = oldVals.getModified();
					double oldVal2 = oldVals.getOriginal();
					double newVal1 = oldVal1+(1.0*normalDist(1, mean, var));
					double newVal2 = oldVal2+(1.0);
					Pair<Double, Double> p = new Pair<Double, Double>();
					p.setOriginal(newVal2);
					p.setModified(newVal1);
					sen.put(parts[0], p);
				}
				else{
					double newVal1 = (1.0*normalDist(1, mean, var));
					double newVal2 = (1.0);
					Pair<Double, Double> p = new Pair<Double, Double>();
					p.setOriginal(newVal2);
					p.setModified(newVal1);
					sen.put(parts[0], p);
				}
			//}
		}
		//System.out.println(sen);
		return new Sentence(sen, Resource.ROGETS);
	}


}
