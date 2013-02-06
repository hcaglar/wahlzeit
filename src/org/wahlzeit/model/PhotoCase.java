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

import java.sql.*;

import org.wahlzeit.services.Persistent;
import org.wahlzeit.services.PersistentReaderWriter;


/**
 * A photo case is a case where someone flagged a photo as inappropriate.
 * 
 * @author dirkriehle
 *
 */
public class PhotoCase extends Case {
	
	/**
	 * 
	 */
	public static final String FLAGGER = "flagger";
	public static final String REASON = "reason";
	public static final String EXPLANATION = "explanation";
	public static final String CREATED_ON = "createdOn";
	public static final String WAS_DECIDED = "wasDecided";
	public static final String DECIDED_ON = "decidedOn";

	/**
	 * 
	 */
	protected CaseId id = CaseId.NULL_ID; // case id
	protected int applicationId = 0; // application id (unused on Java level)
	protected Photo photo = null; // photo id -> photo
	protected String flagger = "unknown";
	protected FlagReason reason = FlagReason.OTHER;
	protected String explanation = "none";	
	protected long createdOn = System.currentTimeMillis();
	protected boolean wasDecided = false;
	protected long decidedOn = 0;
	
	protected void addPersistents()
	{
		addPersistent(new Persistent("id", Integer.class, "0"));
		addPersistent(new Persistent("photo", Integer.class, "0"));
		addPersistent(new Persistent("createdOn", Long.class, ""));
		addPersistent(new Persistent("flagger", String.class, ""));
		addPersistent(new Persistent("explanation", String.class, ""));
		addPersistent(new Persistent("wasDecided", Boolean.class, "false"));
		addPersistent(new Persistent("decidedOn", Long.class, ""));
	}
	
	/**
	 * 
	 */
	public PhotoCase(Photo myPhoto) {
		id = getNextCaseId();
		photo = myPhoto;
		addPersistents();
		incWriteCount();
	}
	
	/**
	 * 
	 */
	public PhotoCase(ResultSet rset) throws SQLException {
		addPersistents();
		readFrom(rset);
	}
	
	/**
	 * 
	 */
	public String getIdAsString() {
		return String.valueOf(id);
	}
	
	/**
	 * 
	 */
	public void readFrom(ResultSet rset) throws SQLException {
		PersistentReaderWriter.readFrom(rset, this);
		id = new CaseId(rset.getInt("id"));
		photo = PhotoManager.getPhoto(PhotoId.getId(rset.getInt("photo")));
		reason = FlagReason.getFromInt(rset.getInt("reason"));
	}
	
	/**
	 * 
	 */
	public void writeOn(ResultSet rset) throws SQLException {
		PersistentReaderWriter.writeOn(rset, this);
		rset.updateInt("id", id.asInt());
		rset.updateInt("photo", (photo == null) ? 0 : photo.getId().asInt());		
	}
	
	/**
	 * 
	 */
	public void writeId(PreparedStatement stmt, int pos) throws SQLException {
		stmt.setInt(pos, id.asInt());
	}
	
	/**
	 * 
	 */
	public CaseId getId() {
		return id;
	}
	
	/**
	 * 
	 */
	public Photo getPhoto() {
		return photo;
	}
	
	/**
	 * 
	 */
	public long getCreationTime() {
		return createdOn;
	}

	/**
	 * 
	 */
	public String getFlagger() {
		return flagger;
	}
	
	/**
	 * 
=	 */
	public void setFlagger(String newFlagger) {
		flagger = newFlagger;
		incWriteCount();
	}
	
	/**
	 * 
	 */
	public FlagReason getReason() {
		return reason;
	}
	
	/**
	 * 
=	 */
	public void setReason(FlagReason newReason) {
		reason = newReason;
		incWriteCount();
	}
	
	/**
	 * 
	 */
	public String getExplanation() {
		return explanation;
	}
	
	/**
	 * 
=	 */
	public void setExplanation(String newExplanation) {
		explanation = newExplanation;
		incWriteCount();
	}
	
	/**
	 * 
	 */
	public boolean wasDecided() {
		return wasDecided;
	}
	
	/**
	 * 
	 */
	public void setDecided() {
		wasDecided = true;
		decidedOn = System.currentTimeMillis();
		incWriteCount();
	}
	
	/**
	 * 
	 */
	public long getDecisionTime() {
		return decidedOn;
	}

	/**
	 * 
	 */
	public String getPhotoOwnerName() {
		return photo.getOwnerName();
	}
	
	/**
	 * 
	 */
	public PhotoStatus getPhotoStatus() {
		return photo.getStatus();
	}
	
}
