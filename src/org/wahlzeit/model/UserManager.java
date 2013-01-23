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

import java.util.*;
import java.sql.*;

import org.wahlzeit.services.*;
import org.wahlzeit.services.mailing.*;

/**
 * The UserManager provides access to and manages Users (including Moderators and Administrators).
 * 
 * @author dirkriehle
 *
 */
public class UserManager extends ObjectManager {

	/**
	 *
	 */
	protected static UserManager instance = new UserManager();

	/**
	 * 
	 */
	public static UserManager getInstance() {
		return instance;
	}
	
	/**
	 * Maps nameAsTag to user of that name (as tag)
	 */
	protected Map<String, UserRole> userRoles = new HashMap<String, UserRole>();
	
	/**
	 * 
	 */
	protected Random codeGenerator = new Random(System.currentTimeMillis());

	/**
	 * 
	 */
	public boolean hasUserByName(String name) {
		assertIsNonNullArgument(name, "user-by-name");
		return hasUserByTag(Tags.asTag(name));
	}
	
	/**
	 * 
	 */
	public boolean hasUserByTag(String tag) {
		assertIsNonNullArgument(tag, "user-by-tag");
		return getUserByTag(tag) != null;
	}
	
	/**
	 * 
	 */
	protected boolean doHasUserByTag(String tag) {
		return doGetUserByTag(tag) != null;
	}
	
	/**
	 * 
	 */
	public UserRole getUserByName(String name) {
		return getUserByTag(Tags.asTag(name));
	}
	
	/**
	 * 
	 */
	public UserRole getUserByTag(String tag) {
		assertIsNonNullArgument(tag, "user-by-tag");

		UserRole result = doGetUserByTag(tag);

		if (result == null) {
			try {
				result = (UserRole) readObject(getReadingStatement("SELECT * FROM users WHERE name_as_tag = ?"), tag);
			} catch (SQLException sex) {
				SysLog.logThrowable(sex);
			}
			
			if (result != null) {
				doAddUser(result);
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 */
	protected UserRole doGetUserByTag(String tag) {
		return userRoles.get(tag);
	}
	
	/**
	 * 
	 * @methodtype factory
	 */
	protected UserRole createObject(ResultSet rset) throws SQLException {
		UserRole result = null;

		AccessRights rights = AccessRights.getFromInt(rset.getInt("rights"));
		ClientRole clientRole;
		ClientCore clientCore = new ClientCore();
		if (rights == AccessRights.USER) {
			clientRole = clientCore.addRole(ClientRole.RoleTypes.USER);
			if(clientRole instanceof UserRole){
			 result = (UserRole)clientRole;
			}
			result.readFrom(rset);
		} else if (rights == AccessRights.MODERATOR) {
			clientRole = clientCore.addRole(ClientRole.RoleTypes.MODERATOR);
			if(clientRole instanceof ModeratorRole){
			 result = (ModeratorRole)clientRole;
			}
			result.readFrom(rset);
		} else if (rights == AccessRights.ADMINISTRATOR) {
			clientRole = clientCore.addRole(ClientRole.RoleTypes.ADMINISTRATOR);
			if(clientRole instanceof AdministratorRole){
			 result = (AdministratorRole)clientRole;
			}
			result.readFrom(rset);
		} else {
			SysLog.logInfo("received NONE rights value");
			clientCore = null;
		}

		return result;
	}
	
	/**
	 * 
	 */
	public void addUser(UserRole userRole) {
		assertIsNonNullArgument(userRole);
		assertIsUnknownUserAsIllegalArgument(userRole);

		try {
			int id = userRole.getId();
			createObject(userRole, getReadingStatement("INSERT INTO users(id) VALUES(?)"), id);
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		doAddUser(userRole);		
	}
	
	/**
	 * 
	 */
	protected void doAddUser(UserRole userRole) {
		userRoles.put(userRole.getNameAsTag(), userRole);
	}
	
	/**
	 * 
	 */
	public void deleteUser(UserRole userRole) {
		assertIsNonNullArgument(userRole);
		doDeleteUser(userRole);

		try {
			deleteObject(userRole, getReadingStatement("DELETE FROM users WHERE id = ?"));
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		assertIsUnknownUserAsIllegalState(userRole);
	}
	
	/**
	 * 
	 */
	protected void doDeleteUser(UserRole userRole) {
		userRoles.remove(userRole.getNameAsTag());
	}
	
	/**
	 * 
	 */
	public void loadUsers(Collection<UserRole> result) {
		try {
			readObjects(result, getReadingStatement("SELECT * FROM users"));
			for (Iterator<UserRole> i = result.iterator(); i.hasNext(); ) {
				UserRole userRole = i.next();
				if (!doHasUserByTag(userRole.getNameAsTag())) {
					doAddUser(userRole);
				} else {
					SysLog.logValueWithInfo("user", userRole.getName(), "user had already been loaded");
				}
			}
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		SysLog.logInfo("loaded all users");
	}
	
	/**
	 * 
	 */
	public long createConfirmationCode() {
		return Math.abs(codeGenerator.nextLong() / 2);
	}
	
	/**
	 * 
	 */
	public void emailWelcomeMessage(UserSession ctx, UserRole userRole) {
		EmailAddress from = ctx.cfg().getAdministratorEmailAddress();
		EmailAddress to = userRole.getEmailAddress();

		String emailSubject = ctx.cfg().getWelcomeEmailSubject();
		String emailBody = ctx.cfg().getWelcomeEmailBody() + "\n\n";
		emailBody += ctx.cfg().getWelcomeEmailUserName() + userRole.getName() + "\n\n"; 
		emailBody += ctx.cfg().getConfirmAccountEmailBody() + "\n\n";
		emailBody += SysConfig.getSiteUrlAsString() + "confirm?code=" + userRole.getConfirmationCode() + "\n\n";
		emailBody += ctx.cfg().getGeneralEmailRegards() + "\n\n----\n";
		emailBody += ctx.cfg().getGeneralEmailFooter() + "\n\n";

		EmailService emailService = EmailServiceManager.getDefaultService();
		emailService.sendEmailIgnoreException(from, to, ctx.cfg().getAuditEmailAddress(), emailSubject, emailBody);
	}
	
	/**
	 * 
	 */
	public void emailConfirmationRequest(UserSession ctx, UserRole userRole) {
		EmailAddress from = ctx.cfg().getAdministratorEmailAddress();
		EmailAddress to = userRole.getEmailAddress();

		String emailSubject = ctx.cfg().getConfirmAccountEmailSubject();
		String emailBody = ctx.cfg().getConfirmAccountEmailBody() + "\n\n";
		emailBody += SysConfig.getSiteUrlAsString() + "confirm?code=" + userRole.getConfirmationCode() + "\n\n";
		emailBody += ctx.cfg().getGeneralEmailRegards() + "\n\n----\n";
		emailBody += ctx.cfg().getGeneralEmailFooter() + "\n\n";

		EmailService emailService = EmailServiceManager.getDefaultService();
		emailService.sendEmailIgnoreException(from, to, ctx.cfg().getAuditEmailAddress(), emailSubject, emailBody);
	}
	
	/**
	 * 
	 */
	public void saveUser(UserRole userRole) {
		try {
			updateObject(userRole, getUpdatingStatement("SELECT * FROM users WHERE id = ?"));
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
	}
	
	/**
	 * 
	 */
	public void removeUser(UserRole userRole) {
		saveUser(userRole);
		userRoles.remove(userRole.getNameAsTag());
	}
	
	/**
	 * 
	 */
	public void saveUsers() {
		try {
			updateObjects(userRoles.values(), getUpdatingStatement("SELECT * FROM users WHERE id = ?"));
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
	}
	
	/**
	 * 
	 */
	public UserRole getUserByEmailAddress(String emailAddress) {
		return getUserByEmailAddress(EmailAddress.getFromString(emailAddress));
	}

	/**
	 * 
	 */
	public UserRole getUserByEmailAddress(EmailAddress emailAddress) {
		UserRole result = null;
		try {
			result = (UserRole) readObject(getReadingStatement("SELECT * FROM users WHERE email_address = ?"), emailAddress.asString());
		} catch (SQLException sex) {
			SysLog.logThrowable(sex);
		}
		
		if (result != null) {
			UserRole current = doGetUserByTag(result.getNameAsTag());
			if (current == null) {
				doAddUser(result);
			} else {
				result = current;
			}
		}

		return result;
	}
	
	/**
	 * 
	 * @methodtype assertion
	 */
	protected void assertIsUnknownUserAsIllegalArgument(UserRole userRole) {
		if (hasUserByTag(userRole.getNameAsTag())) {
			throw new IllegalArgumentException(userRole.getName() + "is already known");
		}
	}
	
	/**
	 * 
	 * @methodtype assertion
	 */
	protected void assertIsUnknownUserAsIllegalState(UserRole userRole) {
		if (hasUserByTag(userRole.getNameAsTag())) {
			throw new IllegalStateException(userRole.getName() + "should not be known");
		}
	}
	
}
