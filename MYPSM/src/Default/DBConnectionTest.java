package Default;




import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

import my.PSM.PSM_Storage.DBConnection;

import org.mockito.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;



@RunWith(MockitoJUnitRunner.class)
public class DBConnectionTest {
	
	/*
	 * ID: DBConnection010 - DBConnection.Java - fetchCourseName(int courseID)
	 * 
	 * Purpose: To verify that DBConnection.fetchCourseName(int courseID)
	 * returns the proper data state variable when it is called
	 * Test Setup: Connection is mocked
	 * 			   Statement is mocked
	 * 			   DBConnection object is initialized
	 * 			   Mockito.When()thenReturn mocks are properly implemented
	 * 
	 * Test Input: db.fetchCourseName(4010)
	 * Expected Output: "Software Testing"
	 */
	 
	 @InjectMocks
	 DBConnection db = new DBConnection();
	
	 
	 @Mock
	 Connection myCon;
	 
	 @Mock
	 Statement s;
	 
	 
	 @Mock
	 ResultSet res;
	
	 @Before
	 public void TestSetup()
	 {
		 s = Mockito.mock(Statement.class);
		 myCon = Mockito.mock(Connection.class);
		 res = Mockito.mock(ResultSet.class);
		 
		 db.setMyCon(myCon);
		 db.setResultSet(res);
		 db.setStatement(s);
	 }
		/*
		 * ID: DBConnection010 - DBConnection.Java - fetchCourseName(int courseID)
		 * 
		 * Purpose: To verify that DBConnection.fetchCourseName(int courseID)
		 * returns the proper data state variable when it is called
		 * Test Setup: Connection is mocked
		 * 			   Statement is mocked
		 * 			   DBConnection object is initialized
		 * 			   Mockito.When()thenReturn mocks are properly implemented
		 * 
		 * Test Input: db.fetchCourseName(4010)
		 * Expected Output: "Software Testing"
		 */
	 @Test
	 public void DBConnection010() throws Exception
	 {
		
		 
		 
		 Mockito.when(myCon.createStatement()).thenReturn(s);
		 Mockito.when(s.executeQuery(Mockito.anyString())).thenReturn(res);
		 Mockito.when(s.getResultSet()).thenReturn(res);
		 Mockito.when(res.getString(Mockito.anyString())).thenReturn("Software Testing");
		 
		 assertEquals("Software Testing", db.fetchCourseName(1));
		 
		 Mockito.verify(res).getString(Mockito.anyString());

	 }		 
	 
	 /*
		 * ID: DBConnection011 - DBConnection.Java - fetchCourseID(int courseID)
		 * 
		 * Purpose: To verify that DBConnection.fetchCourseID(int courseID)
		 * returns the proper data state variable when it is called
		 * Test Setup: Connection is mocked
		 * 			   Statement is mocked
		 * 			   DBConnection object is initialized
		 * 			   Mockito.When()thenReturn mocks are properly implemented
		 * 
		 * Test Input: db.fetchCourseID(907);
		 * Expected Output: 9
		 */
	 @Test //fetCourseID
	 public void DBConnection011() throws Exception
	 {
		 
		 Mockito.when(myCon.createStatement()).thenReturn(s);
		 Mockito.when(s.executeQuery(Mockito.anyString())).thenReturn(res);
		 Mockito.when(s.getResultSet()).thenReturn(res);
		 Mockito.when(res.getInt(Mockito.anyString())).thenReturn(9);
		 
		 assertEquals(9, db.fetchCourseID(907));
		 
		 Mockito.verify(res).getInt(Mockito.anyString());
	}
	
	 /*
		 * ID: DBConnection010 - DBConnection.Java - fetchCourseName(int courseID)
		 * 
		 * Purpose: To verify that DBConnection.fetchCourseName(int courseID)
		 * returns the proper data state variable when it is called
		 * Test Setup: Connection is mocked
		 * 			   Statement is mocked
		 * 			   DBConnection object is initialized
		 * 			   Mockito.When()thenReturn mocks are properly implemented
		 * 
		 * Test Input: db.fetchCourseName(4010)
		 * Expected Output: "Software Testing"
		 */
	 
	 @Test
	 public void DBConnection013() throws Exception
	 {
		 ArrayList<Integer> testArray = new ArrayList<Integer>();
	 }
	 /*
	 @Test (expected = SQLException.class)
	 public void DBConnection014() throws SQLException
	 {
		
		 
		 //Mockito.doThrow(Exception.class).when(db).fetchCourseName(Mockito.anyInt());
		 Mockito.when(myCon.createStatement()).thenThrow(new SQLException());
		 String testString = db.fetchCourseName(4097);
		 
		 
		 

	 }	
	 */
	
	
	
	 
}
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
