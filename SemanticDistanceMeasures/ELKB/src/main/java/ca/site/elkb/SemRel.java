/*
 * Copyright (C) Johannes Fähndrich - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 * Written by Johannes Fähndrich <faehndrich@gmail.com.com>,  2011
 *
 */

package ca.site.elkb;

/**
 * Represents a <i>Roget's Thesaurus</i> relation between a word or phrase.
 * This can be a Cross-reference or a See reference. For example:
 * <ul>
 * <li>See <i>drug taking</i></li>
 * <li>646 <i>perfect</i></li>
 * </ul>
 * Relation types currently used by the <i>ELKB</i> are <TT>cref</TT> and
 * <TT>see</TT>.
 * 
 * @author Mario Jarmasz and Alistsair Kennedy
 * @version 1.4 2013
 */

public class SemRel extends Reference {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5128354271966119550L;
	// Attributes
	private String type;

	/**
	 * Default constructor.
	 */
	public SemRel() {
		super();
		type = new String();
	}

	/**
	 * Constructor which sets the relation type, Head number and Reference name.
	 * 
	 * @param t
	 * @param headNum
	 * @param refName
	 */
	public SemRel(String t, int headNum, String refName) {
		this();
		type = t;
		setHeadNum(headNum);
		setRefName(refName);
	}

	/**
	 * Constructor which sets the relation type, Head number and Reference name.
	 * 
	 * @param t
	 * @param headNum
	 * @param refName
	 * @param pos
	 * @param paraNum
	 * @param sgNum
	 */
	public SemRel(String t, int headNum, String refName, String pos, int paraNum, int sgNum) {
		this(t, headNum, refName);
		setPos(pos);
		// setParaNum(paraNum);
		// setSgNum(sgNum);
	}

	/**
	 * Returns the relation type.
	 * 
	 * @return returns type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the relation type.
	 * 
	 * @param t
	 */
	public void setType(String t) {
		type = t;
	}

	/**
	 * Converts to a string representation the <TT>SemRel</TT> object.
	 */
	public String toString() {
		String info = new String();
		info = "SemRel" + "@" + Integer.toHexString(hashCode());
		info += "@" + getType();
		info += "@" + getHeadNum() + "@" + getRefName();
		// info += getPos() + "@" + getParaNum() + "@" + getSgNum();
		return info;
	}

	/**
	 * Prints this relation to the standard output.
	 */
	public void print() {
		String info = new String();
		info = getType() + ": " + getHeadNum() + " " + getRefName();
		System.out.println(info);
	}

}
