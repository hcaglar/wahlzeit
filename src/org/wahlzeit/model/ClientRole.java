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

import java.util.List;

import org.wahlzeit.services.EmailAddress;

public abstract class ClientRole implements Client {

	/**
	 * 
	 */
	protected ClientCore core;
	
	/*
	 * 
	 */
	public ClientRole(ClientCore core) {
		this.core = core;
	}
	
	/**
	 * 
	 */
	public enum RoleTypes 
	{
		GUEST, 
		USER, 
		MODERATOR, 
		ADMINISTRATOR
	}

	/**
	 * @methodtype get
	 */
	public AccessRights getRights(){
		return core.getRights();
	}
	
	/**
	 * @methodtype set
	 */
	public void setRights(AccessRights newRights){
		core.setRights(newRights);
	}
	/**
	 * 
	 * @methodtype boolean-query
	 */
	public boolean hasRights(AccessRights otherRights){
		return core.hasRights(otherRights);
	}
	
	/**
	 * 
	 * @methodtype boolean-query
	 */
	public boolean hasGuestRights(){
		return core.hasGuestRights();
	}
	
	/**
	 * 
	 */
	public boolean hasUserRights(){
		return core.hasUserRights();
	}
	
	/**
	 * 
	 * @methodtype boolean-query
	 */
	public boolean hasModeratorRights(){
		return core.hasModeratorRights();
	}
	
	/**
	 * 
	 * @methodtype boolean-query
	 */
	public boolean hasAdministratorRights(){
		return core.hasAdministratorRights();
	}
	
	/**
	 * 
	 * @methodtype get
	 */
	public EmailAddress getEmailAddress(){
		return core.getEmailAddress();
	}
	
	/**
	 * 
	 * @methodtype set
	 */
	public void setEmailAddress(EmailAddress newEmailAddress){
		core.setEmailAddress(newEmailAddress);
	}
	
	/**
	 * 
	 */
	public ClientRole addRole(ClientRole.RoleTypes role){
		return core.addRole(role);
	}
	
	/**
	 * 
	 */
	public ClientRole getRole(ClientRole.RoleTypes role){
		return core.getRole(role);
	}
	/**
	 * 
	 */
	public static ClientRole createRole(RoleTypes role, ClientCore core) {
		switch (role) {
		case GUEST:
			return new GuestRole(core);
		case USER:
			return new UserRole(core);
		case MODERATOR:
			return new ModeratorRole(core);
		case ADMINISTRATOR:
			return new AdministratorRole(core);
		default:
			return null;
		}
	}

	
}
