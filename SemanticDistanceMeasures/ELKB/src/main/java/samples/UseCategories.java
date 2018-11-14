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
 * Examples of using the categories class in the Roget's ELKB.  Categories is used
 * to gain information about the upper levels of the Thesaurus including Head, Head Group
 * Sub-Section, Section and Class.
 * 
 * These examples include counting the number of heads, and retrieving a list of information
 * about the heads.
 * 
 * @author Alistair Kennedy
 *
 */
public class UseCategories {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//query a word
		ca.site.elkb.RogetELKB elkb = new ca.site.elkb.RogetELKB("1911X5");
		
		//count heads
		int headCount = elkb.category.getHeadCount();
		System.out.println("Number of heads: " + headCount);
		
		//Get list of head information
		ArrayList<ca.site.elkb.HeadInfo> heads = elkb.category.getHeadList();
		System.out.println("List of heads");
		for(ca.site.elkb.HeadInfo hi : heads){
			System.out.println(hi);
		}
	}
}
