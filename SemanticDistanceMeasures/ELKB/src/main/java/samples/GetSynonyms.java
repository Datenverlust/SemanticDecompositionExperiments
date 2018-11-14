/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package samples;

import java.util.ArrayList;

/**
 * Examples for retrieving the terms from a semicolon group.  The word "lead" is searched in
 * the index and then  every Verb semicolon group containing the word "lead" is printed out.
 * 
 * @author Alistair Kennedy
 *
 */
public class GetSynonyms {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//load ELKB
		ca.site.elkb.RogetELKB elkb = new ca.site.elkb.RogetELKB("1911X5");
		System.out.println("Semicolon Gorups containing \"lead\"");
		
		//numberical references
		ArrayList<int[]> al2 = elkb.index.getEntryListNumerical("lead");
		for(int[] array : al2){
			ca.site.elkb.Head h = elkb.text.getHead(array[4]);
			String partOfSpeech = elkb.index.convertToPOS(array[5]);
			if(partOfSpeech.equals("VB.")){
				ca.site.elkb.Paragraph p = h.getPara(array[6]-1, partOfSpeech);
				ca.site.elkb.SG semicolonGroup = p.getSG(array[7]-1);
				System.out.println(semicolonGroup.getWordList());
			}
		}
	}

}
