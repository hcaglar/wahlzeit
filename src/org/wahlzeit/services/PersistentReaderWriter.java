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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class PersistentReaderWriter{

	/**
	 * 
	 */
	public void readFrom(ResultSet rset, PersistentType persType)
			throws SQLException {
		Iterator<Persistent> iterator = persType.getPersistentList().iterator();
		while (iterator.hasNext()) {
			Persistent pers = iterator.next();

			if (Integer.class == pers.getType()) {
				pers.setValue(String.valueOf(rset.getInt(pers.getName())));
			} else if (String.class == pers.getType()) {
				pers.setValue(rset.getString(pers.getName()));
			} else if (Boolean.class == pers.getType()) {
				pers.setValue(String.valueOf(rset.getBoolean(pers.getName())));
			} else if (Long.class == pers.getType()) {
				pers.setValue(String.valueOf(rset.getLong(pers.getName())));
			} else if (Float.class == pers.getType()) {
				pers.setValue(String.valueOf(rset.getFloat(pers.getName())));
			} else {
				throw new IllegalStateException("Unknown persistent type");
			}
		}
	}

	/**
	 * 
	 */
	public void writeOn(ResultSet rset, PersistentType persType)
			throws SQLException {
		Iterator<Persistent> iterator = persType.getPersistentList().iterator();
		while (iterator.hasNext()) {
			Persistent pers = iterator.next();

			if (Integer.class == pers.getType()) {
				rset.updateInt(pers.getName(), Integer.valueOf(pers.getValue()));
			} else if (String.class == pers.getType()) {
				rset.updateString(pers.getName(), pers.getValue());

			} else if (Boolean.class == pers.getType()) {
				rset.updateBoolean(pers.getName(),
						Boolean.valueOf(pers.getValue()));
			} else if (Long.class == pers.getType()) {
				rset.updateLong(pers.getName(), Long.valueOf(pers.getValue()));
			} else if (Float.class == pers.getType()) {
				rset.updateFloat(pers.getName(), Float.valueOf(pers.getValue()));
			} else {
				throw new IllegalStateException("Unknown persistent type");
			}
		}
	}

}
