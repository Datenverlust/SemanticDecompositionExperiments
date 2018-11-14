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
 * This example uses the Text class to retrieve a paragraph and a semicolon group
 * and all its words from the Thesaurus.
 * 
 * @author Alistair Kennedy
 *
 */
public class UseText {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//create ELKB object
		ca.site.elkb.RogetELKB elkb = new ca.site.elkb.RogetELKB("1911X5");
		
		//get head number 12
		ca.site.elkb.Head h12 = elkb.text.getHead(12);
		
		//get the first noun paragraph from the head
		ca.site.elkb.Paragraph p1N = h12.getPara(0, "N.");
		
		//get the words and phrases from the paragraph and print them.
		ArrayList<String> paragraphWords = p1N.getAllWordList();
		System.out.println(paragraphWords);
		
		//get the first semicolon group in the paragraph
		ca.site.elkb.SG sg1N = p1N.getSG(0);
		
		//get all words in the semicolon group and print them.
		ArrayList<String> sgWords = sg1N.getAllWordList();
		System.out.println(sgWords);
		
	}
}
