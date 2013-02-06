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

/**
 * A Persistent object is one that can be read from and written to a RDMBS.
 * Also, it has a write count, which serves as a dirty flag.
 * 
 * @author dirkriehle
 *
 */
public class Persistent {

	protected PersistentAttribute attr;

	public Persistent(String name, Class type, String value) {
		attr = new PersistentAttribute(name, type, value);
	}

	public String getName() {
		return attr.getName();
	}

	public Class getType() {
		return attr.getType();
	}

	public String getValue() {
		return attr.getValue();
	}

	public void setValue(String value) {
		attr.setValue(value);
	}
}
