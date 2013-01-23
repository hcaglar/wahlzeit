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

import org.wahlzeit.model.AccessRights;
import org.wahlzeit.model.Photo;
import org.wahlzeit.model.UserRole;
import org.wahlzeit.model.UserSession;
import org.wahlzeit.utils.HtmlUtil;
import org.wahlzeit.webparts.WebPart;



/**
 * 
 * @author dirkriehle
 *
 */
public class ShowUserProfileFormHandler extends AbstractWebFormHandler {
	
	/**
	 *
	 */
	public ShowUserProfileFormHandler() {
		initialize(PartUtil.SHOW_USER_PROFILE_FORM_FILE, AccessRights.USER);
	}
	
	/**
	 * 
	 */
	protected void doMakeWebPart(UserSession ctx, WebPart part) {
		UserRole userRole = (UserRole) ctx.getClient();

		Photo photo = userRole.getUserPhoto();
		part.addString(Photo.THUMB, getPhotoThumb(ctx, photo));
		
		part.maskAndAddString(UserRole.NAME, userRole.getName());
		part.addString(UserRole.STATUS, ctx.cfg().asValueString(userRole.getStatus()));
		part.maskAndAddString(UserRole.EMAIL_ADDRESS, userRole.getEmailAddress().asString());
		part.addString(UserRole.MEMBER_SINCE, ctx.cfg().asDateString(userRole.getCreationTime()));
		part.addString(UserRole.NOTIFY_ABOUT_PRAISE, ctx.cfg().asYesOrNoString(userRole.getNotifyAboutPraise()));
		part.addString(UserRole.HOME_PAGE, HtmlUtil.asHref(userRole.getHomePage().toString()));
		part.addString(UserRole.NO_PHOTOS, String.valueOf(userRole.getNoPhotos()));
		part.addString(UserRole.GENDER, ctx.cfg().asValueString(userRole.getGender()));
		part.addString(UserRole.LANGUAGE, ctx.cfg().asValueString(userRole.getLanguage()));
	}

	/**
	 * 
	 */
	protected String doHandlePost(UserSession ctx, Map args) {
		return PartUtil.EDIT_USER_PROFILE_PAGE_NAME;
	}
	
}
