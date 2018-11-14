/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package sentRep;

public class Pair<T, S> {
	private T modified;
	private S original;
	
	public Pair(){
		modified = null;
		original = null;
	}
	
	/*private Pair(S orig, T mod){
		modified = mod;
		original = orig;
	}*/
	
	public void setModified(T val){
		modified = val;
	}
	
	public void setOriginal(S val){
		original = val;
	}
	
	public T getModified(){
		return modified;
	}
	
	public S getOriginal(){
		return original;
	}
	
	public String toString(){
		return "("+original+","+modified+")";
	}
}
