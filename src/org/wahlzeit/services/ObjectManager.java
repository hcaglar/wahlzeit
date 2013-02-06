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

import java.sql.*;
import java.util.*;

/**
 * An ObjectManager creates/reads/updates/deletes PersistentInterface (objects) from a (relational) Database.
 * It is an abstract superclass that relies an inheritance interface and the PersistentInterface.
 * Subclasses for specific types of object need to implement createObject and provide Statements.
 * 
 * @author dirkriehle
 *
 */
public abstract class ObjectManager {
	
	protected ArrayList<PersistentType> persistentTypeList = new ArrayList<PersistentType>();
	/**
	 * 
	 */
	public DatabaseConnection getDatabaseConnection() throws SQLException {
		return ContextManager.getDatabaseConnection();
	}
	    
	/**
	 * 
	 */
	protected PreparedStatement getReadingStatement(String stmt) throws SQLException {
    	DatabaseConnection dbc = getDatabaseConnection();
    	return dbc.getReadingStatement(stmt);
	}
	
	/**
	 * 
	 */
	protected PreparedStatement getUpdatingStatement(String stmt) throws SQLException {
    	DatabaseConnection dbc = getDatabaseConnection();
    	return dbc.getUpdatingStatement(stmt);
	}
	
	/**
	 * 
	 */
	protected PersistentInterface readObject(PreparedStatement stmt, int value) throws SQLException {
		PersistentInterface result = null;
		stmt.setInt(1, value);
		SysLog.logQuery(stmt);
		ResultSet rset = stmt.executeQuery();
		if (rset.next()) {
			result = createObject(rset);
		}

		return result;
	}
	
	/**
	 * 
	 */
	protected PersistentInterface readObject(PreparedStatement stmt, String value) throws SQLException {
		PersistentInterface result = null;
		stmt.setString(1, value);
		SysLog.logQuery(stmt);
		ResultSet rset = stmt.executeQuery();
		if (rset.next()) {
			result = createObject(rset);
		}

		return result;
	}
	
	/**
	 * 
	 */
	protected void readObjects(Collection result, PreparedStatement stmt) throws SQLException {
		SysLog.logQuery(stmt);
		ResultSet rset = stmt.executeQuery();
		while (rset.next()) {
			PersistentInterface obj = createObject(rset);
			result.add(obj);
		}
	}
		
	/**
	 * 
	 */
	protected void readObjects(Collection result, PreparedStatement stmt, String value) throws SQLException {
		stmt.setString(1, value);
		SysLog.logQuery(stmt);
		ResultSet rset = stmt.executeQuery();
		while (rset.next()) {
			PersistentInterface obj = createObject(rset);
			result.add(obj);
		}
	}
		
	/**
	 * 
	 */
	protected abstract PersistentInterface createObject(ResultSet rset) throws SQLException;

	/**
	 * 
	 */
	protected void createObject(PersistentInterface obj, PreparedStatement stmt, int value) throws SQLException {
		stmt.setInt(1, value);
		SysLog.logQuery(stmt);
		stmt.executeUpdate();
	}
	
	/**
	 * 
	 */
	protected void createObject(PersistentInterface obj, PreparedStatement stmt, String value) throws SQLException {
		stmt.setString(1, value);
		SysLog.logQuery(stmt);
		stmt.executeUpdate();
	}
	
	/**
	 * 
	 */
	protected void updateObject(PersistentType obj, PreparedStatement stmt) throws SQLException {
		if (obj.isDirty()) {
			obj.writeId(stmt, 1);
			SysLog.logQuery(stmt);
			ResultSet rset = stmt.executeQuery();
			if (rset.next()) {
				PersistentReaderWriter.writeOn(rset, obj);
				rset.updateRow();
				updateDependents(obj);
				obj.resetWriteCount();
			} else {
				SysLog.logError("trying to update non-existent object: " + obj.getIdAsString() + "(" + obj.toString() + ")");
			}
		}
	}
	
	/**
	 * 
	 */
	protected void updateObjects(Collection coll, PreparedStatement stmt) throws SQLException {
		for (Iterator i = coll.iterator(); i.hasNext(); ) {
			PersistentType obj = (PersistentType) i.next();
			updateObject(obj, stmt);
		}
	}
	
	/**
	 * 
	 */
	protected void updateDependents(PersistentInterface obj) throws SQLException {
		// do nothing
	}
	
	/**
	 * 
	 */
	protected void deleteObject(PersistentInterface obj, PreparedStatement stmt) throws SQLException {
		obj.writeId(stmt, 1);
		SysLog.logQuery(stmt);
		stmt.executeUpdate();
	}

	/**
	 * 
	 */
	protected void assertIsNonNullArgument(Object arg) {
		assertIsNonNullArgument(arg, "anonymous");
	}
	
	/**
	 * 
	 */
	protected void assertIsNonNullArgument(Object arg, String label) {
		if (arg == null) {
			throw new IllegalArgumentException(label + " should not be null");
		}
	}

}
