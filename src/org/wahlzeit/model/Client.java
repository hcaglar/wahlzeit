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

package org.wahlzeit.model;

import org.wahlzeit.services.*;

/**
 * A Client uses the system. It is an abstract superclass.
 * This package defines guest, user, moderator, and administrator clients.
 * 
 * @author dirkriehle
 *
 */
public interface Client {
	
	/**
	 * @methodtype get
	 */
	public AccessRights getRights();
	
	/**
	 * @methodtype set
	 */
	public void setRights(AccessRights newRights);
	
	/**
	 * 
	 * @methodtype boolean-query
	 */
	public boolean hasRights(AccessRights otherRights);
	
	/**
	 * 
	 * @methodtype boolean-query
	 */
	public boolean hasGuestRights();
	
	/**
	 * 
	 */
	public boolean hasUserRights();
	
	/**
	 * 
	 * @methodtype boolean-query
	 */
	public boolean hasModeratorRights();
	
	/**
	 * 
	 * @methodtype boolean-query
	 */
	public boolean hasAdministratorRights();
	
	/**
	 * 
	 * @methodtype get
	 */
	public EmailAddress getEmailAddress();
	
	/**
	 * 
	 * @methodtype set
	 */
	public void setEmailAddress(EmailAddress newEmailAddress);
	
	/**
	 * @return TODO
	 * 
	 */
	public ClientRole addRole(ClientRole.RoleTypes role);
	
	/**
	 * 
	 */
	public ClientRole getRole(ClientRole.RoleTypes role);

}
