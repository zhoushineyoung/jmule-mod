/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2008 JMule team ( jmule@jmule.org / http://jmule.org )
 *
 *  Any parts of this program derived from other projects, or contributed
 *  by third-party developers are copyrighted by their respective authors.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package org.jmule.core.jkad.routingtable;

import org.jmule.core.jkad.Int128;

/**
 * Created on Dec 28, 2008
 * @author binary256
 * @version $Revision: 1.5 $
 * Last changed by $Author: binary255 $ on $Date: 2010/06/28 17:46:03 $
 */
public class Node {

	private Node left = null;
	private Node right = null;
	private Node parent = null;
	private Int128 nodeIndex;
	private int nodeLevel;
	
	private int hashCode = 0;	
	private KBucket subnet = null;
	
	public Node(Node parent, Int128 nodeIndex, int nodeLevel) {
		this.parent = parent;
		this.subnet = null;
		this.nodeIndex = nodeIndex;
		this.nodeLevel = nodeLevel;
		hashCode = getIDAsString().hashCode(); 
	}
	
	public Node(Node parent,Int128 nodeIndex, int nodeLevel, KBucket kBucket) {
		this(parent, nodeIndex, nodeLevel);
		this.subnet = kBucket;
	}
	
	public void addContact(KadContact contact) {
		if (subnet.isFull()) {
			splitNode();
			if (contact.getContactDistance().getBit(nodeLevel))
				right.addContact(contact);
			else
				left.addContact(contact);
			return ;
		}
		subnet.add(contact);
	}

	/**
	 * Split node into two sub nodes when KBucket is full
	 */
	private void splitNode() {
		Int128 left_index = nodeIndex.clone();
		left_index.setBit(nodeLevel, false);
		
		Int128 right_index = nodeIndex.clone();
		right_index.setBit(nodeLevel, true);
		
		KBucket left_subnet = new KBucket();
		KBucket righ_subnet = new KBucket();
		
		for(KadContact contact : subnet.getContacts()) {
			
			if (contact.getContactDistance().getBit(nodeLevel))
				righ_subnet.add(contact);
			else
				left_subnet.add(contact);
		}
		
		Node left_node = new Node(this,left_index, nodeLevel+1, left_subnet);
		Node right_node = new Node(this, right_index, nodeLevel+1, righ_subnet);
		
		this.left  = left_node;
		this.right = right_node;
		
		subnet.clear();
		subnet = null;
	}

	String getIDAsString() {
		String result = ""; 
		for(int i=0;i<nodeLevel;i++) {
			if (nodeIndex.getBit(i))
				result+="1";
			else
				result += "0";
		}
		if (nodeLevel==0) result+="<root>";
		return result;
	}
	
	public String toString() {
		String result = "";
		
		result = "Node index : " + getIDAsString(); 
		
		result += "\n";
		result += "Level     : " + getLevel() + "\n"; 
		if (getParent()!=null)
		result += "Parent    : " + getParent().getIDAsString() + "\n";
		else
		result += "Parent    : null \n";
		result += "KBucket   : " + getKBucket() +"\n";
		
		if (getLeft()!=null)
			result += "Left      : " + getLeft().getIDAsString() + "\n";
		
		if (getRight()!=null)
			result += "Right     : " + getRight().getIDAsString()+"\n";
		
		return result;
	}
	
	public KBucket getKBucket() {
		return subnet;
	}
	
	public Node getLeft() {
		return left;
	}
	
	public void setLeft(Node node) {
		left = node;
	}
	
	public Node getRight() {
		return right;
	}
	
	public void setRight(Node node) {
		right = node;
	}
	
	public boolean isLeaf() {
		return subnet != null;
	}
	
	public Int128 getIndex() {
		return nodeIndex;
	}
	
	public int getLevel() {
		return nodeLevel;
	}

	public Node getParent() {
		return parent;
	}

	public int hashCode() {
		return hashCode;
		//return nodeIndex.hashCode();
	}
	
	public boolean equals(Object object) {
		if (object == null) return false;
		if (!(object instanceof Node)) return false;
		Node node = (Node)object;
		return node.hashCode() == hashCode();
	}
	
}
