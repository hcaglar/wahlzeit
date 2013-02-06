package org.wahlzeit.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface PersistentInterface {
	public void readFrom(ResultSet rset, PersistentType persType)
			throws SQLException;

	public void writeOn(ResultSet rset, PersistentType persType)
			throws SQLException;
	
	 
		/**
		 * 
		 */
		public boolean isDirty();
		
		/**
		 * 
		 */
		public void incWriteCount();
		
		/**
		 * 
		 */
		public void resetWriteCount();

		/**
		 * 
		 */
		public String getIdAsString();
		
		/**
		 * 
		 */
		public void writeId(PreparedStatement stmt, int pos) throws SQLException;
		
}
