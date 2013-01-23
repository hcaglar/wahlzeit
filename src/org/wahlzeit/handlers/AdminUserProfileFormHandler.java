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

package org.wahlzeit.handlers;

import java.util.*;

import org.wahlzeit.model.*;
import org.wahlzeit.services.*;
import org.wahlzeit.utils.*;
import org.wahlzeit.webparts.*;

/**
 * 
 * @author dirkriehle
 *
 */
public class AdminUserProfileFormHandler extends AbstractWebFormHandler {
	
	/**
	 *
	 */
	public AdminUserProfileFormHandler() {
		initialize(PartUtil.ADMIN_USER_PROFILE_FORM_FILE, AccessRights.ADMINISTRATOR);
	}
	
	/**
	 * 
	 */
	protected void doMakeWebPart(UserSession ctx, WebPart part) {
		Map<String, Object> args = ctx.getSavedArgs();

		String userId = ctx.getAndSaveAsString(args, "userId");
		UserRole userRole = UserManager.getInstance().getUserByName(userId);
	
		Photo photo = userRole.getUserPhoto();
		part.addString(Photo.THUMB, getPhotoThumb(ctx, photo));

		part.maskAndAddString("userId", userRole.getName());
		part.maskAndAddString(UserRole.NAME, userRole.getName());
		part.addSelect(UserRole.STATUS, UserStatus.class, (String) args.get(UserRole.STATUS));
		part.addSelect(UserRole.RIGHTS, AccessRights.class, (String) args.get(UserRole.RIGHTS));
		part.addSelect(UserRole.GENDER, Gender.class, (String) args.get(UserRole.GENDER));
		part.addSelect(UserRole.LANGUAGE, Language.class, (String) args.get(UserRole.LANGUAGE));
		part.maskAndAddStringFromArgsWithDefault(args, UserRole.EMAIL_ADDRESS, userRole.getEmailAddress().asString());
		part.maskAndAddStringFromArgsWithDefault(args, UserRole.HOME_PAGE, userRole.getHomePage().toString());
		
		if (userRole.getNotifyAboutPraise()) {
			part.addString(UserRole.NOTIFY_ABOUT_PRAISE, HtmlUtil.CHECKBOX_CHECK);
		}
	}

	/**
	 * 
	 */
	protected String doHandlePost(UserSession ctx, Map args) {
		UserManager um = UserManager.getInstance();
		String userId = ctx.getAndSaveAsString(args, "userId");
		UserRole userRole = um.getUserByName(userId);
		
		String status = ctx.getAndSaveAsString(args, UserRole.STATUS);
		String rights = ctx.getAndSaveAsString(args, UserRole.RIGHTS);
		String gender = ctx.getAndSaveAsString(args, UserRole.GENDER);
		String language = ctx.getAndSaveAsString(args, UserRole.LANGUAGE);
		String emailAddress = ctx.getAndSaveAsString(args, UserRole.EMAIL_ADDRESS);
		String homePage = ctx.getAndSaveAsString(args, UserRole.HOME_PAGE);
		String notifyAboutPraise = ctx.getAndSaveAsString(args, UserRole.NOTIFY_ABOUT_PRAISE);
		
		if (!StringUtil.isValidStrictEmailAddress(emailAddress)) {
			ctx.setMessage(ctx.cfg().getEmailAddressIsInvalid());
			return PartUtil.SHOW_ADMIN_PAGE_NAME;
		} else if (!StringUtil.isValidURL(homePage)) {
			ctx.setMessage(ctx.cfg().getUrlIsInvalid());
			return PartUtil.SHOW_ADMIN_PAGE_NAME;
		}
		
		userRole.setStatus(UserStatus.getFromString(status));
		userRole.setRights(AccessRights.getFromString(rights));
		userRole.setGender(Gender.getFromString(gender));
		userRole.setLanguage(Language.getFromString(language));
		userRole.setEmailAddress(EmailAddress.getFromString(emailAddress));
		userRole.setHomePage(StringUtil.asUrl(homePage));
		userRole.setNotifyAboutPraise((notifyAboutPraise != null) && notifyAboutPraise.equals("on"));

		um.removeUser(userRole);
		userRole = um.getUserByName(userId);
		ctx.setSavedArg("userId", userId);

		StringBuffer sb = UserLog.createActionEntry("AdminUserProfile");
		UserLog.addUpdatedObject(sb, "User", userRole.getName());
		UserLog.log(sb);
		
		ctx.setMessage(ctx.cfg().getProfileUpdateSucceeded());

		return PartUtil.SHOW_ADMIN_PAGE_NAME;
	}
	
}
