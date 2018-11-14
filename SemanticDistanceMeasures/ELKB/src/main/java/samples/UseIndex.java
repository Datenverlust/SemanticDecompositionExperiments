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
 * The index allows you to find locations of a word in Roget's Thesaurus.  References to the
 * location of a word can come in two forms.  One is a string indicating the paragraph name
 * followed by head number e.g. <i>relation #9</i>.
 * 
 * The other option is to get references as an integer array containing 9 numbers: The class number,
 * section number, subsection number, headgroup number, head number, POS number, Paragraph number,
 * semicolon group number and word number.  This methos is generally much faster since the precise 
 * location of a word can be found without having to search through the head file using the <i>text</i>
 * class.
 * 
 * @author Alistair Kennedy
 *
 */
public class UseIndex {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//load ELKB
		ca.site.elkb.RogetELKB elkb = new ca.site.elkb.RogetELKB("1911X5");
		System.out.println("Search results for animal");
		
		//string references
		ArrayList<String> al1 = elkb.index.getEntryList("animal");
		System.out.println("text references");
		System.out.println(al1);
		
		//numberical references
		ArrayList<int[]> al2 = elkb.index.getEntryListNumerical("animal");
		System.out.println("numerical refereces");
		for(int[] array : al2){
			for(int i : array){
				System.out.print(i + " ");
			}
			System.out.println();
		}
	}
}
