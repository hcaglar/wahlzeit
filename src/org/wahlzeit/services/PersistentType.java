/*
 * Copyright (c) 2006-2009 by Dirk Riehle, http://dirkriehle.com
 *
 * This file is part of the Wahlzeit photo rating application.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package org.wahlzeit.services;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ArrayList;

public class PersistentType implements PersistentInterface{
	
	protected ArrayList<Persistent> persistentList = new ArrayList<Persistent>();
	
	public PersistentType() {
		
	}
	
	/**
	 * Not used in the class but needed by broad array of subclasses
	 */
	public static String ID = "id";

	/**
	 * 
	 */
	protected transient int writeCount = 0;

	
	/**
	 * 
	 */
	public boolean isDirty() {
		return writeCount != 0;
	}
	
	/**
	 * 
	 */
	public final void incWriteCount() {
		writeCount++;
	}
	
	/**
	 * 
	 */
	public void resetWriteCount() {
		writeCount = 0;
	}
	
	/**
	 * 
	 */
	public String getIdAsString() {
		return ID;
	}

	/**
	 * 
	 */
	public void writeId(PreparedStatement stmt, int pos) throws SQLException {
		stmt.setString(pos, ID);
	}
	
	
	/**
	 * Returns true when persistent obj. is added
	 */
	public boolean addPersistent(Persistent pers) {
		Iterator<Persistent> iterator = persistentList.iterator();
        while(iterator.hasNext()){
                if(iterator.next().getName() == pers.getName()){
                	return false;
                }
        }
		persistentList.add(pers);
		return true;
	}

	/**
	 * Returns the removed persistent obj.
	 */
	public Persistent removePersistent(String name) {
		Persistent ret = null;
        for (int i=0; i< persistentList.size(); i++)
        {
        	if(name == persistentList.get(i).getName()) {
        		ret = persistentList.get(i);
        		persistentList.remove(i);
        		break;
        	}
        }
        return ret;
	}
	
	/**
	 * Returns true when persistent obj. is removed
	 */
	public boolean removePersistent(Persistent pers) {
		return persistentList.remove(pers);
	}
	
	 public ArrayList<Persistent> getPersistentList(){
		 return persistentList;
	 }
	 
	 

}
