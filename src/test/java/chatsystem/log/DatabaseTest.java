package chatsystem.log;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class DatabaseTest {
	
	Database db = Database.getInstance();
	
	@Test
	void testDatabase() throws TableAlreadyExists, SQLException {
		db.removeTable("testTable");
		db.newTable("testTable");
		db.addToTable("testTable", "Emilie", "Hey!");
		db.addToTable("testTable", "Daniel", "Hello :)");
		
		ResultSet rs = db.getTable("testTable");

		if (!rs.isBeforeFirst()) {
			fail("No data found in table");
		}
		
		while (rs.next()) {
			String from = rs.getString("fromContact");
			String msg = rs.getString("msg");
			
			if (from.equals("Emilie")) {
				assert msg.equals("Hey!");
			}
			else if (from.equals("Daniel")) {
				assert msg.equals("Hello :)");
			}
			else {
				fail("Invalid 'fromContact' value found in table");
			}
		}
		rs.close();
		
		db.removeTable("testTable");
	}
	
	@Test
	void testTableAlreadyExists() throws TableAlreadyExists, SQLException {
		db.removeTable("testTableExists");
		db.newTable("testTableExists");
		
		try {
			db.newTable("testTableExists");
			db.removeTable("testTableExists");
			fail("Expected TableAlreadyExists exception");
		} catch (TableAlreadyExists e) {
			// expected outcome
		}
	}
}
