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
public class EditUserProfileFormHandler extends AbstractWebFormHandler {
	
	/**
	 *
	 */
	public EditUserProfileFormHandler() {
		initialize(PartUtil.EDIT_USER_PROFILE_FORM_FILE, AccessRights.USER);
	}
	
	/**
	 * @methodtype command
	 */
	protected void doMakeWebPart(UserSession ctx, WebPart part) {
		Map<String, Object> args = ctx.getSavedArgs();
		part.addStringFromArgs(args, UserSession.MESSAGE);

		UserRole userRole = (UserRole) ctx.getClient();
		part.maskAndAddString(UserRole.NAME, userRole.getName());

		Photo photo = userRole.getUserPhoto();
		part.addString(Photo.THUMB, getPhotoThumb(ctx, photo));
		part.addSelect(UserRole.GENDER, Gender.class, (String) args.get(UserRole.GENDER), userRole.getGender()); 
		part.addSelect(UserRole.LANGUAGE, Language.class, (String) args.get(UserRole.LANGUAGE), userRole.getLanguage());
		
		part.maskAndAddStringFromArgsWithDefault(args, UserRole.EMAIL_ADDRESS, userRole.getEmailAddress().asString());
		
		part.addString(UserRole.NOTIFY_ABOUT_PRAISE, HtmlUtil.asCheckboxCheck(userRole.getNotifyAboutPraise()));

		part.maskAndAddStringFromArgsWithDefault(args, UserRole.HOME_PAGE, userRole.getHomePage().toString());
	}

	/**
	 * 
	 */
	protected String doHandlePost(UserSession ctx, Map args) {
		String emailAddress = ctx.getAndSaveAsString(args, UserRole.EMAIL_ADDRESS);
		String homePage = ctx.getAndSaveAsString(args, UserRole.HOME_PAGE);
		String gender = ctx.getAndSaveAsString(args, UserRole.GENDER);
		String language = ctx.getAndSaveAsString(args, UserRole.LANGUAGE);
		
		if (!StringUtil.isValidStrictEmailAddress(emailAddress)) {
			ctx.setMessage(ctx.cfg().getEmailAddressIsInvalid());
			return PartUtil.EDIT_USER_PROFILE_PAGE_NAME;
		} else if (!StringUtil.isValidURL(homePage)) {
			ctx.setMessage(ctx.cfg().getUrlIsInvalid());
			return PartUtil.EDIT_USER_PROFILE_PAGE_NAME;
		}
		
		UserRole userRole = (UserRole) ctx.getClient();
		
		userRole.setEmailAddress(EmailAddress.getFromString(emailAddress));
	
		String status = ctx.getAndSaveAsString(args, UserRole.NOTIFY_ABOUT_PRAISE);
		boolean notify = (status != null) && status.equals("on");
		userRole.setNotifyAboutPraise(notify);

		userRole.setHomePage(StringUtil.asUrl(homePage));
		
		if (!StringUtil.isNullOrEmptyString(gender)) {
			userRole.setGender(Gender.getFromString(gender));
		}
		
		if (!StringUtil.isNullOrEmptyString(language)) {
			Language langValue = Language.getFromString(language);
			ctx.setConfiguration(LanguageConfigs.get(langValue));
			userRole.setLanguage(langValue);
		}
		
		StringBuffer sb = UserLog.createActionEntry("EditUserProfile");
		UserLog.addUpdatedObject(sb, "User", userRole.getName());
		UserLog.log(sb);
		
		ctx.setTwoLineMessage(ctx.cfg().getProfileUpdateSucceeded(), ctx.cfg().getContinueWithShowUserHome());

		return PartUtil.SHOW_NOTE_PAGE_NAME;
	}
	
}
