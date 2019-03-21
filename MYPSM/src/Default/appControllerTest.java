package Default;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
//import org.powermock.reflect.*;

import my.PSM.PSM_Interface.LoginForm;
import my.PSM.PSM_Interface.MainMenu;
import my.PSM.PSM_Interface.Messages;
import my.PSM.PSM_Logic.*;
import my.PSM.PSM_Storage.*;
//import my.PSM.MainMenu;
import my.PSM.PSM_Logic.appController;
import my.PSM.PSM_Storage.DBConnection;



@PowerMockIgnore("javax.swing.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({Thread.class, System.class, appController.class}) //Put the static class in here
public class appControllerTest {

	DBConnection initialDb = new DBConnection();
	
	@Mock
	DBConnection db;//= Mockito.mock(DBConnection.class);
	
	appController appController;// = new appController();
	
	@Before
	public void setUp() throws Exception 
	{
		appController = new appController();
		
		initialDb = appController.db;
		appController.db = db;
		
		//appController.db = PowerMockito.mock(DBConnection.class);
		
		//MockitoAnnotations.initMocks(appController.class);
	}

	@After
	public void tearDown() throws Exception 
	{
		appController.db = initialDb;
		appController = null;
	}
	
	/* ID: AppCont001 - appController.java - checkClear()
	 * Purpose: To assert that checkClear() checks if the calendar is reset and returns true
	 * Test Setup: 
	 * 		appController instantiated
	 * 		ArrayList <String> endDates;
	 * 		endDates.add("01/01/12");
	 * Test Input:
	 * 		1) checkClear()
	 * Expected Output: checkClear() returns true
	 */
	@Test
	public void AppCont001()
	{
		//Preconditions
		ArrayList <String> endDates = new ArrayList<String>();
		
		endDates.add("01/01/12"); //course completed should return true
		
		Mockito.when(db.getEndDates()).thenReturn(endDates);
		
		//Test Input
		boolean answer = appController.checkClear();
		
		//Assertions
		assertEquals(true, answer);
		
		Mockito.verify(db).getEndDates();
	}
	
	/* ID: AppCont002 - appController.java - checkTimes()
	 * Purpose: To test that the checkTimes() method schedules the popup for
	 * 		5 min, 15 min, and the end of class, if there is a course on that day.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		ArrayList<Integer> mockCourseList;
	 * 		mockCourseList.add(1);
	 * 		Set each class end time to 6:15.
	 * 
	 * Test Input:
	 * 		checkTimes()
	 * 
	 * Expected Output: 
	 * 		For endofclass:
	 * 			hr = 6;
	 * 			min+1 = 15;
	 * 		For popup5min:
	 * 			hr = 6;
	 * 			min+5 = 15;
	 * 		For popup15min:
	 * 			hr =6;
	 * 			min+15 = 15;
	 */		
	@Test
	public void AppCont002() throws Exception
	{
		//Preconditions
		ArrayList<Integer> mockCourseList = new ArrayList<Integer>();
		mockCourseList.add(1);
		
		Mockito.when(db.getCourses()).thenReturn(mockCourseList);
		//set internal variables for all days
		Mockito.when(db.fetchEndMon(1)).thenReturn("06:15");
		Mockito.when(db.fetchEndTue(1)).thenReturn("06:15");
		Mockito.when(db.fetchEndWed(1)).thenReturn("06:15");
		Mockito.when(db.fetchEndThu(1)).thenReturn("06:15");
		Mockito.when(db.fetchEndFri(1)).thenReturn("06:15");
		Mockito.when(db.fetchEndSat(1)).thenReturn("06:15");
		
		//Test Input
		appController.checkTimes();
		
		
		//Accessing private state variables
		TimerTask popup5min = (TimerTask) Whitebox.getInternalState(appController, "popup5min");
		TimerTask popup15min = (TimerTask) Whitebox.getInternalState(appController, "popup15min");
		TimerTask endofclass = (TimerTask) Whitebox.getInternalState(appController, "endofclass");
		
		
		//Force execution to update the scheduledExecutionTime()
		popup5min.run();
		popup15min.run();
		endofclass.run();
		
		//Get the current time of the System from the method
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(endofclass.scheduledExecutionTime());
		
		int hr = cal.get(cal.HOUR);
		int min = cal.get(cal.MINUTE) + 1; //end of class subtracts one
		
	
		//Assertions
		assertEquals(6, hr);
		assertEquals(15, min);
		
		cal.setTimeInMillis(popup5min.scheduledExecutionTime());
		hr = cal.get(cal.HOUR);
		min = cal.get(cal.MINUTE);
		assertEquals(6, hr);
		assertEquals(15, min+5);
		
		cal.setTimeInMillis(popup15min.scheduledExecutionTime());
		hr = cal.get(cal.HOUR);
		min = cal.get(cal.MINUTE);
		assertEquals(6, hr);
		assertEquals(15, min+15);
		
	
	}
	
	/* ID: AppCont003 - appController.java - getData(int course)
	 * Purpose: To ensure that the instance variables are set to the data
	 * 	from the database.
	 * Test Setup: 
	 * 		appController instantiated
	 * 		appController.db mocked
	 * 		Database data mocked
	 * Test Input:
	 * 		1) getData(0) 
	 * 
	 * Expected Output: 
	 * 		defSub = "CEN"
	 * 		defSemester = "Spring"
	 *		defCourseName = "Software"
	 *		For all course starts = "5:00"
	 *		For all course ends = "6:15"
	 */
	@Test
	public void AppCont003()
	{
		//Preconditions
		int courseNum = 0;
		Mockito.when(db.fetchCourseSubj(courseNum)).thenReturn("CEN");
		Mockito.when(db.fetchCourseSemester(courseNum)).thenReturn("Spring");
		Mockito.when(db.fetchCourseName(courseNum)).thenReturn("Software");
		Mockito.when(db.fetchCourseStart(courseNum)).thenReturn("5:00");
		Mockito.when(db.fetchCourseEnd(courseNum)).thenReturn("6:15");
		Mockito.when(db.fetchStartMon(courseNum)).thenReturn("5:00");
		Mockito.when(db.fetchStartTue(courseNum)).thenReturn("5:00");
		Mockito.when(db.fetchStartWed(courseNum)).thenReturn("5:00");
		Mockito.when(db.fetchStartThu(courseNum)).thenReturn("5:00");
		Mockito.when(db.fetchStartFri(courseNum)).thenReturn("5:00");
		Mockito.when(db.fetchStartSat(courseNum)).thenReturn("5:00");
		Mockito.when(db.fetchEndMon(courseNum)).thenReturn("6:15");
		Mockito.when(db.fetchEndTue(courseNum)).thenReturn("6:15");
		Mockito.when(db.fetchEndWed(courseNum)).thenReturn("6:15");
		Mockito.when(db.fetchEndThu(courseNum)).thenReturn("6:15");
		Mockito.when(db.fetchEndFri(courseNum)).thenReturn("6:15");
		Mockito.when(db.fetchEndSat(courseNum)).thenReturn("6:15");
		
		//Test Input
		appController.getData(courseNum);
		
		Mockito.verify(db).fetchCourseSubj(courseNum);
		
		//Assertions
		//Check if all state variables changed correctly
		assertEquals("CEN", appController.defSub);
		assertEquals("Spring", appController.defSemester);
		assertEquals("Software", appController.defCourseName);
		assertEquals("5:00", appController.defCourseStart);
		assertEquals("6:15", appController.defCourseEnd);
		assertEquals("5:00", appController.defMonStart);
		assertEquals("6:15", appController.defMonEnd);
		assertEquals("5:00", appController.defTueStart);
		assertEquals("6:15", appController.defTueEnd);
		assertEquals("5:00", appController.defWedStart);
		assertEquals("6:15", appController.defWedEnd);
		assertEquals("5:00", appController.defThuStart);
		assertEquals("6:15", appController.defThuEnd);
		assertEquals("5:00", appController.defFriStart);
		assertEquals("6:15", appController.defFriEnd);
		assertEquals("5:00", appController.defSatStart);
		assertEquals("6:15", appController.defSatEnd);
		
	}
	
	
	/* ID: AppCont004 - appController.java - sleep(int milli)
	 * Purpose: To ensure that the current thread is set to sleep for 100 milliseconds
	 * 		with the sleep(int milli) method.
	 * Test Setup: 
	 * 		appController instantiated
	 * 		PowerMockito.mockStatic(Thread.class);
	 * 
	 * Test Input:
	 * 		appController.sleep(100);
	 * 
	 * Expected Output: 
	 * 		Verified true that the mocked Thread.class was called.
	 */		
	@Test
	public void AppCont004() throws Exception
	{
		//Preconditions
		PowerMockito.mockStatic(Thread.class);
	
		//Test Input
		appController.sleep(100);
		
		//Assertions
		PowerMockito.verifyStatic(Thread.class);
		
	}
	
	/* ID: AppCont005 - appController.java - LogIn()
	 * 
	 * Purpose: To verify that the appController sets the loggedIn state to true
	 * 		when LogIn() is called.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		String user = "username";
	 * 		String pass = "password";
	 * 		Mockito.when(db.connect(user, pass)).thenReturn(0);
	 * 		Set appController private variables: 
	 * 			username = user;
	 * 			password = pass;
	 * 
	 * Test Input:
	 * 		appController.LogIn();
	 * 
	 * Expected Output: 
	 * 		Variable loggedIn in the appController should return true.
	 */
	@Test
	public void AppCont005()
	{
		//Preconditions
		String user = "username";
		String pass = "password";
		Mockito.when(db.connect(user, pass)).thenReturn(0);
		Whitebox.setInternalState(appController, "username", user);
		Whitebox.setInternalState(appController, "password", pass);

		//TestInput
		appController.LogIn();
		
		//Assertions
		boolean logged = (boolean) Whitebox.getInternalState(appController, "loggedin");
		
		Mockito.verify(db).connect(user, pass);
		
		assertEquals(true, logged);
	}
	
	/* ID: AppCont006 - appController.java - getCon()
	 * 
	 * Purpose: To ensure that the getCon() method returns the correct 
	 * 		DBConnection object.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		
	 * Test Input:
	 * 		appController.getCon();
	 * 
	 * Expected Output: 
	 * 		The DBConnection db returned from getCon() should be the 
	 * 		same reference as appController.db.
	 */
	@Test
	public void AppCont006()
	{
		//Preconditions
		
		//Test Input
		DBConnection db = appController.getCon();
		
		//Assertions
		assertEquals(appController.db, db);
	}
	
	/* ID: AppCont007 - appController.java - TimerTask dbClear
	 * 
	 * Purpose: To test that the dbClear.run() method attempts to clear the 
	 * 		database.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		Mock the call to clearDatabase() to do nothing.
	 * 		Access dbClear from appController.
	 * 
	 * Test Input:
	 * 		dbClear.run()		
	 * Expected Output: 
	 * 		db.clearDatabase() was called which means the run() method was called.
	 */
	@Test
	public void AppCont007()
	{
		//Preconditions
		Mockito.doNothing().when(db).clearDatabase();
		
		TimerTask dbClear = (TimerTask) Whitebox.getInternalState(appController, "dbClear");
		
		//Test Input
		dbClear.run();
		
		//Assertions
		Mockito.verify(db).clearDatabase();
	}
	
	/* ID: AppCont008 - appController.java - TimerTask popup15min
	 * 
	 * Purpose: To test that the popup15min.run() attempts to display a 
	 * 		fifteen minute warning message.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		Mock the messages class
	 *		Do nothing when msg.FifteenMinWarning().
	 *		Access popup15min variable from appController.
	 *
	 * Test Input:
	 *		popup15min.run() 		
	 * Expected Output: 
	 * 		ic.msg.FifteenMinWarning() was attempted to be called but was stubbed.
	 */
	//@Test
	public void AppCont008()
	{
		//Preconditions
		Messages msg = Mockito.mock(Messages.class);
		InterfaceController ic = Mockito.mock(InterfaceController.class);
		Mockito.doNothing().when(msg).FifteenMinWarning();
		
		TimerTask popup15min = (TimerTask) Whitebox.getInternalState(appController, "popup15min");
		
		//Test Input
		popup15min.run();
		
		//Assertions
		Mockito.verify(msg).FifteenMinWarning();
	}
	
	/* ID: AppCont009 - appController.java - TimerTask popup5min 
	 *
	 * Purpose: To test that the popup5min.run() attempts to display a 
	 * 		five minute warning message.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		Mock the messages class
	 *		Do nothing when msg.FiveMinWarning().
	 *		Access popup5min variable from appController.
	 *
	 * Test Input:
	 *		popup5min.run() 		
	 * Expected Output: 
	 * 		ic.msg.FiveMinWarning() was attempted to be called but was stubbed.
	 */
	//@Test
	public void AppCont009()
	{
		//Preconditions
		Messages msg = Mockito.mock(Messages.class);
		
		//InterfaceController ic = (InterfaceController) Whitebox.getInternalState(appController, "ic");
		Mockito.doNothing().when(msg).FiveMinWarning();
		
		TimerTask popup5min = (TimerTask) Whitebox.getInternalState(appController, "popup5min");
		
		//Test Input
		popup5min.run();
		
		//Assertions
		Mockito.verify(msg).FiveMinWarning();
	}
	
	/* ID: AppCont010 - appController.java - TimerTask endofclass
	 * 
	 * Purpose: To test that the endofclass.run() method attempts to display a
	 * 		end of class message and changes the classEnded state variable to the current
	 * 		System time.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		Mock the messages class.
	 *		Do nothing when msg.endClassWarning() is called.
	 *		Access endofclass variable from appController.
	 *
	 * Test Input:
	 * 		endofclass.run();
	 * 
	 * Expected Output: 
	 * 		ic.msg.endClassWarning() was attempted to be called and
	 * 		the classEnded variable was set to the current time.
	 */
	//@Test
	public void AppCont010()
	{
		//Preconditions
		Messages msg = Mockito.mock(Messages.class);
		
		//InterfaceController ic = (InterfaceController) Whitebox.getInternalState(appController, "ic");
		Mockito.doNothing().when(msg).endClassWarning();
		TimerTask endofclass= (TimerTask) Whitebox.getInternalState(appController, "endofclass");
		
		//Test Input
		endofclass.run();
		
		//Assertions
		Mockito.verify(msg).endClassWarning();
		
		long classEnded = (long) Whitebox.getInternalState(appController, "classEnded");
		assertEquals(System.currentTimeMillis(), classEnded);
	}
	

	/* ID: AppCont011 - appController.java - TimerTask systemExit
	 * 
	 * Purpose: To test that the systemExit.run() method attempts to run the
	 * 		System.exit(0) command.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		PowerMock the static class System.
	 * 		
	 * Test Input:
	 * 		systemExit.run();
	 * Expected Output: 
	 * 		System.exit(0) was attempted to be executed but was stubbed.
	 */
	//@Test
	public void AppCont011() throws InterruptedException
	{
		//Preconditions
		PowerMockito.mockStatic(System.class);
		
		TimerTask systemExit = (TimerTask) Whitebox.getInternalState(appController, "systemExit");
		
		//Test Input
		systemExit.run(); 
		
		//Assertions
		PowerMockito.verifyStatic(System.class);
	}
	
	/* ID: AppCont012 - appController.java - setTime(...)
	 * 
	 * Purpose: To test if the setTime() method in appController sets the
	 * 		Calendar setRun state variable to the specified time.
	 * Test Setup: 
	 * 		appController instantiated
	 * 		int year = 2012;
	 * 		int month = 1;
	 * 		int date = 25;
	 * 		int hours = 10;
	 * 		int min = 0;
	 * 
	 * Test Input:
	 * 		appController.setTime(year, month, date, hours, min);
	 * 
	 * Expected Output: 
	 * 		setRun state variable is set to the time 1/25/2012 10:45.
	 */
	@Test
	public void AppCont012()
	{
		//Preconditions
		int year = 2012;
		int month = 1;
		int date = 25;
		int hours = 10;
		int min = 45;
		
		//Test Input
		appController.setTime(year, month, date, hours, min);
		
		//Assertions
		
		//Whitebox can access private state variables
		Calendar cal = (Calendar) Whitebox.getInternalState(appController, "setRun");
		Date dateAssert = cal.getTime();
		
		assertEquals(dateAssert, appController.getTime());
		
	}
	
	/* ID: AppCont013 - appController.java - getTime()
	 * 
	 * Purpose: To test if the getTime() method returns the time set
	 * 		using setTime().
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		int year = 2012;
	 * 		int month = 1;
	 * 		int date = 25;
	 * 		int hours = 10;
	 * 		int min = 0;
	 * 		appController.setTime(year, month, date, hours, min);
	 * 
	 * Test Input:
	 * 		appController.getTime();
	 * 
	 * Expected Output: 
	 * 		getTime() returns the same value as the time set in the 
	 * 		assertDate variable.
	 */
	@Test
	public void AppCont013()
	{
		//Preconditions
		int year = 2012;
		int month = 1;
		int date = 25;
		int hours = 10;
		int min = 0;
	
		appController.setTime(year, month, date, hours, min);
		
		//Test Input
		Date answer = appController.getTime();
		
		//Assertions
		Calendar cal = (Calendar) Whitebox.getInternalState(appController, "setRun");
		
		cal.set(year, month, date, hours, min);
		Date assertDate = cal.getTime();
		
		assertEquals(assertDate, answer);
	}
	
	/* ID: AppCont014 - appController.java - getTimeMillis()
	 * 
	 * Purpose: To test that the getTimeMillis() method returns the time
	 * 	in milliseconds since the default time defined in the Calendar class after
	 * 	the time was set with setTime().
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		int year = 2012;
	 * 		int month = 1;
	 * 		int date = 25;
	 * 		int hours = 10;
	 * 		int min = 45;
	 * 
	 * 		appController.setTime(year, month, date, hours, min);
		
	 * Test Input:
	 * 		appController.getTimeMillis();
	 * 
	 * Expected Output: 
	 * 		getTimeMillis() should return the value set using setTime() 
	 * 		in milliseconds.
	 */
	@Test
	public void AppCont014()
	{
		//Preconditions
		int year = 2012;
		int month = 1;
		int date = 25;
		int hours = 10;
		int min = 45;
		
		appController.setTime(year, month, date, hours, min);
		
		//Test Input
		long answer = appController.getTimeMillis();
		
		//Assertions
		
		//Setting the calendar's milliseconds to the correct value
		Calendar cal = (Calendar) Whitebox.getInternalState(appController, "setRun");
		cal.set(year, month, date, hours, min);
		long assertDate = cal.getTimeInMillis();
		
		assertEquals(assertDate, answer);
	}
	
	/* ID: AppCont015 - appController.java - timerParser()
	 * 
	 * Purpose: To test that the timerParser() method parses the inputted
	 * 		time into the respective state variables.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		String time = "10:15"
	 * 
	 * Test Input:
	 * 		appController.timerParser(time);
	 * 
	 * Expected Output: 
	 * 		State variables:
	 * 			hr = 10
	 * 			min = 15
	 */
	@Test
	public void AppCont015()
	{
		//Preconditions
		String time = "10:15";
		
		//Test Input
		appController.timerParser(time);
		
		//Assertions
		
		//Accessing private state variables
		int hr = (int) Whitebox.getInternalState(appController, "hr");
		int min = (int) Whitebox.getInternalState(appController, "min");
		
		assertEquals(10, hr);
		assertEquals(15, min);
	}
	
	/* ID: AppCont016 - appController.java - dateParser()
	 * 
	 * Purpose: To test that the dateParser() method parses the inputted date
	 * 		into the respective state variables.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		String date = "12/25/19";
	 * 
	 * Test Input:
	 * 		appController.dateParser(date);
	 * 
	 * Expected Output: 
	 * 		State variables:
	 * 			clearMonth = 12
	 * 			clearDate = 25
	 * 			clearYear = 19
	 */
	@Test
	public void AppCont016()
	{
		//Preconditions
		String date = "12/25/19";
		
		//Test Input
		appController.dateParser(date);
		
		//Assertions
		int clearMonth = (int) Whitebox.getInternalState(appController,"clearMonth");
		int clearDate = (int) Whitebox.getInternalState(appController,"clearDate");
		int clearYear = (int) Whitebox.getInternalState(appController,"clearYear");
		
		assertEquals(12, clearMonth);
		assertEquals(25, clearDate);
		assertEquals(19, clearYear);
	}

	/* ID: AppCont017 - appController.java - returnHr()
	 * 
	 * Purpose: 
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		String time = "12:25"
	 * 		
	 * Test Input:
	 * 		appController.returnHr()
	 * Expected Output: 
	 */
	@Test
	public void AppCont017()
	{
		//Preconditions
		String time = "12:25";
		appController.timerParser(time);
		
		//Test Input
		int hr = appController.returnHr();
		
		//Assertions
		assertEquals(12, hr);
	}
	
	/* ID: AppCont018 - appController.java - returnMin()
	 * 
	 * Purpose: To verify that the returnMin() method returns the correct
	 * 		value for the min state variable.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		String time = "12:25"
	 * 		timerParser(time)
	 * 
	 * Test Input:
	 * 		appController.returnMin()
	 * 
	 * Expected Output: 
	 * 		State variables:
	 * 			min = 25
	 */
	@Test
	public void AppCont018()
	{
		//Preconditions
		String time = "12:25";
		appController.timerParser(time);
		
		//Test Input
		int min = appController.returnMin();
		
		//Assertions
		assertEquals(25, min);
	}
	
	/* ID: AppCont019 - appController.java - getEndTime()
	 * 
	 * Purpose: To test the getEndTime() method returns todays Date but 1 mins
	 * 		before the specified parameter's time.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		int hrs = 10;
	 * 		int mins = 15;
	 * 
	 * Test Input:
	 * 		appController.getEndTime(hrs, mins);
	 * 	
	 * Expected Output: 
	 * 		The date returned by getEndTime() should be today's date at
	 * 		10:14.
	 */
	@Test
	public void AppCont019()
	{
		//Preconditions
		int hrs = 10;
		int mins = 15;
		
		//Test Input
		Date answer = appController.getEndTime(hrs, mins);
		
		//Assertions
		DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm");
		String date = simple.format(answer).toString();
		
		int hr = Integer.parseInt(date.substring(12, 14));
		int min = Integer.parseInt(date.substring(15, 17));
		
		assertEquals(hrs, hr);
		assertEquals(mins-1, min);
	}
	
	/* ID: AppCont020 - appController.java - setSemesterClear()
	 * 
	 * Purpose: To test that the setSemesterClear() method updates the autoClear
	 * 		and date2 state variable to the specified inputs.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		int year = 2012;
	 * 		int mon = 12;
	 * 		int date = 25;
	 * 		int hrs = 10;
	 * 		int min = 15;
	 * 		
	 * 
	 * Test Input:
	 * 		appController.setSemesterClear(year, mon, date, hrs, min)
	 * 
	 * Expected Output: 
	 * 		State variable autoClear should be set to the inputted time, and
	 * 		state variable date2 should be set to the date set in the autoClear
	 * 		variable.
	 * 		
	 */	
	@Test
	public void AppCont020()
	{
		//Preconditions
		int year = 2012;
		int mon = 12;
		int date = 25;
		int hrs = 10;
		int min = 15;
		
		//Test Input
		appController.setSemesterClear(year, mon, date, hrs, min);
		
		//Assertions	
		
		//Accessing private variables
		Calendar autoClear = (Calendar) Whitebox.getInternalState(appController, "autoClear");
		
		//Setting this calendar's milliseconds to the correct value
		Calendar cal = autoClear;
		cal.set(year, mon, date, hrs, min);
		assertEquals(cal, autoClear); //Asserting autoClear variable was changed
		
		Date date2 = (Date) Whitebox.getInternalState(appController, "date2");
		Date assertDate = cal.getTime();
		assertEquals(assertDate, date2); //Asserting date2 variable was changed
	}
	
	/* ID: AppCont021 - appController.java - getSemesterClear()
	 * 
	 * Purpose: To test that the getSemesterClear() method returns the date
	 * 		of the autoClear state variable.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		int year = 2012;
	 * 		int mon = 12;
	 * 		int date = 25;
	 * 		int hrs = 10;
	 * 		int min = 15;
	 * 		
	 * 		appController.setSemesterClear(year, mon, date, hrs, min);
	 * 
	 * Test Input:
	 * 		appController.getSemesterClear()
	 * 
	 * Expected Output: 
	 * 		getSemesterClear() returns the same date as Calendar set with the
	 * 		same inputs.
	 */
	@Test
	public void AppCont021()
	{
		//Preconditions
		int year = 2012;
		int mon = 12;
		int date = 25;
		int hrs = 10;
		int min = 15;
		
		appController.setSemesterClear(year, mon, date, hrs, min);
		
		//Test Input
		Date answer = appController.getSemesterClear();
		
		//Assertions
		
		//Setting this calendar's milliseconds to the correct value
		Calendar cal = (Calendar) Whitebox.getInternalState(appController, "autoClear");
		cal.set(year, mon, date, hrs, min);
		
		assertEquals(cal.getTime(), answer);
	}
	
	
	/* ID: AppCont022 - appController.java - get15BeforeEnd()
	 * 
	 * Purpose: To test the get15BeforeEnd() method returns todays Date but 15 mins
	 * 		before the specified parameter's time.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		int hrs = 10;
	 * 		int mins = 15;
	 * 
	 * Test Input:
	 * 		appController.get15BeforeEnd(hrs, mins);
	 * 		
	 * Expected Output: 
	 * 		The date returned by get15BeforeEnd() should be today's date at
	 * 		10:00.
	 */
	@Test
	public void AppCont022()
	{
		//Preconditions
		int hrs = 10;
		int mins = 15;
		
		//Test Input
		Date answer = appController.get15BeforeEnd(hrs, mins);
		
		//Setting date into simple format to access time
		DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm");
		String date = simple.format(answer).toString();
		
		int hr = Integer.parseInt(date.substring(12, 14));
		int min = Integer.parseInt(date.substring(15, 17));
		
		//Assertions
		assertEquals(hrs, hr);
		assertEquals(mins-15, min);
	}
	
	/* ID: AppCont023 - appController.java - get5BeforeEnd()
	 * 
	 * Purpose: To test the get5BeforeEnd() method returns todays Date but 5 mins
	 * 		before the specified parameter's time.
	 * 
	 *Test Setup: 
	 * 		appController instantiated
	 * 		int hrs = 10;
	 * 		int mins = 15;
	 * 
	 * Test Input:
	 * 		appController.get5BeforeEnd(hrs, mins);
	 * 		
	 * Expected Output: 
	 * 		The date returned by get5BeforeEnd() should be today's date at
	 * 		10:10.
	 */
	@Test
	public void AppCont023()
	{
		//Preconditions
		int hrs = 10;
		int mins = 15;
		
		//Test Input
		Date answer = appController.get5BeforeEnd(hrs, mins);
		
		//Setting date into simple format to access time
		DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm");
		String date = simple.format(answer).toString();
		
		int hr = Integer.parseInt(date.substring(12, 14));
		int min = Integer.parseInt(date.substring(15, 17));
		
		//Assertions
		assertEquals(hrs, hr);
		assertEquals(mins-5, min);
        
	}
	

	/* ID: AppCont024 - appController.java - autoExit()
	 * 
	 * Purpose: To test that the autoExit() method schedules a system exit for 
	 * 		immediate execution. 
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		Mock static class System.
	 * 		Initialize date to 1/1/2012.
	 * 
	 * Test Input:
	 * 		appController.autoExit()
	 * 
	 * Expected Output: 
	 * 		System.exit(0) should be attempted to be called when 
	 * 		the run() function is called from the systemExit TimerTask in
	 * 		the appController.
	 */
	//@Test
	public void AppCont024() throws Exception
	{
		//Preconditions
		PowerMockito.mockStatic(System.class);
		
		Calendar cal = new GregorianCalendar();
		cal.set(2012, 1, 1);
		Date date = cal.getTime();
		Whitebox.setInternalState(appController, "date", date);
		
		//Test Input
		appController.autoExit();
		
		//Assertions
		PowerMockito.verifyStatic(System.class);
		
	
	}

	/* ID: AppCont025 - appController.java - autoClear()
	 * 
	 * Purpose: To test that the autoClear() method schedules to clear the database
	 * 		for immediate execution. 
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		Mock database and do nothing when db.clearDatabase() is called.
	 * 		Initialize date to 1/1/2012.
	 * 
	 * Test Input:
	 * 		appController.autoClear();
	 * 
	 * Expected Output: 
	 * 		The autoClear() method attempted to call db.clearDatabase() through
	 * 		the run() function in TimerTask dbClear.
	 */
	//@Test
	public void AppCont025()
	{
		//Preconditions
		Mockito.doNothing().when(db).clearDatabase();
		
		Calendar cal = new GregorianCalendar();
		cal.set(2012, 1, 1);
		Date date = cal.getTime();
		Whitebox.setInternalState(appController, "date2", date);
		
		//Test Input
		appController.autoClear();
		
		//Assertions
		
		Mockito.verify(db).clearDatabase();
	}
	
	//RAINY DAYS

	/* ID: AppCont026 - appController.java - checkClear()
	 * 
	 * Purpose: To assert that checkClear() checks if the calendar is reset and returns false
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		ArrayList <String> endDates;
	 * 		endDates.add("01/01/20");
	 * 
	 * Test Input:
	 * 		1) checkClear()
	 * 
	 * Expected Output: checkClear() returns false
	 */
	@Test
	public void AppCont026()
	{
		//Preconditions
		ArrayList <String> endDates = new ArrayList<String>();
		
		endDates.add("01/01/20"); //course completed should return true
		
		Mockito.when(db.getEndDates()).thenReturn(endDates);
		
		//Test Input
		boolean answer = appController.checkClear();
		
		//Assertions
		assertEquals(false, answer);
		
		Mockito.verify(db).getEndDates();
	}
	
	/* ID: AppCont027 - appController.java - checkTimes()
	 * Purpose: To test that the checkTimes() method doesn't schedule the popup for
	 * 		5 min, 15 min, and the end of class since there are no courses.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		ArrayList<Integer> mockCourseList;
	 * 		mockCourseList.add(1);
	 * 		Set each class end time to "".
	 * 
	 * Test Input:
	 * 		checkTimes()
	 * Expected Output: 
	 * 		All popup execution times are set to 0.
	 */		
	@Test
	public void AppCont027() throws Exception
	{
		//Preconditions
		ArrayList<Integer> mockCourseList = new ArrayList<Integer>();
		mockCourseList.add(1);
		
		Mockito.when(db.getCourses()).thenReturn(mockCourseList);
		//set internal variables for all days
		Mockito.when(db.fetchEndMon(1)).thenReturn("");
		Mockito.when(db.fetchEndTue(1)).thenReturn("");
		Mockito.when(db.fetchEndWed(1)).thenReturn("");
		Mockito.when(db.fetchEndThu(1)).thenReturn("");
		Mockito.when(db.fetchEndFri(1)).thenReturn("");
		Mockito.when(db.fetchEndSat(1)).thenReturn("");
		
		//Test Input
		appController.checkTimes();
		
		
		//Accessing private state variables
		TimerTask popup5min = (TimerTask) Whitebox.getInternalState(appController, "popup5min");
		TimerTask popup15min = (TimerTask) Whitebox.getInternalState(appController, "popup15min");
		TimerTask endofclass = (TimerTask) Whitebox.getInternalState(appController, "endofclass");
		
		
		assertEquals(0, popup5min.scheduledExecutionTime());
		assertEquals(0, popup15min.scheduledExecutionTime());
		assertEquals(0, endofclass.scheduledExecutionTime());
	}
	
	/* ID: AppCont028 - appController.java - getData(int course)
	 * 
	 * Purpose: To ensure that the instance variables are empty string when
	 * 		no data is returned from the DBConnection
	 * Test Setup: 
	 * 		appController instantiated
	 * 		appController.db mocked
	 * 		Database data mocked
	 * Test Input:
	 * 		getData(0) 
	 * 
	 * Expected Output: 
	 * 		Every state variable state variable should be empty string.
	 */
	@Test
	public void AppCont028() 
	{
		//Preconditions
		int courseNum = 0;
		Mockito.when(db.fetchCourseSubj(courseNum)).thenReturn("");
		Mockito.when(db.fetchCourseSemester(courseNum)).thenReturn("");
		Mockito.when(db.fetchCourseName(courseNum)).thenReturn("");
		Mockito.when(db.fetchCourseStart(courseNum)).thenReturn("");
		Mockito.when(db.fetchCourseEnd(courseNum)).thenReturn("");
		Mockito.when(db.fetchStartMon(courseNum)).thenReturn("");
		Mockito.when(db.fetchStartTue(courseNum)).thenReturn("");
		Mockito.when(db.fetchStartWed(courseNum)).thenReturn("");
		Mockito.when(db.fetchStartThu(courseNum)).thenReturn("");
		Mockito.when(db.fetchStartFri(courseNum)).thenReturn("");
		Mockito.when(db.fetchStartSat(courseNum)).thenReturn("");
		Mockito.when(db.fetchEndMon(courseNum)).thenReturn("");
		Mockito.when(db.fetchEndTue(courseNum)).thenReturn("");
		Mockito.when(db.fetchEndWed(courseNum)).thenReturn("");
		Mockito.when(db.fetchEndThu(courseNum)).thenReturn("");
		Mockito.when(db.fetchEndFri(courseNum)).thenReturn("");
		Mockito.when(db.fetchEndSat(courseNum)).thenReturn("");
		
		//Test Input
		appController.getData(courseNum);
		
		Mockito.verify(db).fetchCourseSubj(courseNum);
		
		//Assertions
		//Check if all state variables changed correctly
		assertEquals("", appController.defSub);
		assertEquals("", appController.defSemester);
		assertEquals("", appController.defCourseName);
		assertEquals("", appController.defCourseStart);
		assertEquals("", appController.defCourseEnd);
		assertEquals("", appController.defMonStart);
		assertEquals("", appController.defMonEnd);
		assertEquals("", appController.defTueStart);
		assertEquals("", appController.defTueEnd);
		assertEquals("", appController.defWedStart);
		assertEquals("", appController.defWedEnd);
		assertEquals("", appController.defThuStart);
		assertEquals("", appController.defThuEnd);
		assertEquals("", appController.defFriStart);
		assertEquals("", appController.defFriEnd);
		assertEquals("", appController.defSatStart);
		assertEquals("", appController.defSatEnd);
		
	}
	
	
	/* ID: AppCont029 - appController.java - sleep(int milli)
	 * Purpose: To test that when the Thread class attempts to sleep it throws
	 * 		an exception.
	 * Test Setup: 
	 * 		appController instantiated
	 * 		PowerMockito.mockStatic(Thread.class);
	 * 
	 * Test Input:
	 * 		appController.sleep(100);
	 * 
	 * Expected Output: 
	 * 		sleep() throws an exception.
	 */		
	@Test(expected = Exception.class)
	public void AppCont029() throws Exception
	{
		//Preconditions
		PowerMockito.mockStatic(Thread.class);
		PowerMockito.when(Thread.class).thenThrow(Exception.class);
		
		//Test Input
		appController.sleep(100);
		
		//Assertions
		
		
	}
	
	/* ID: AppCont030 - appController.java - LogIn()
	 * 
	 * Purpose: To verify that the appController sets the loggedIn state to false
	 * 		when LogIn() is called.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		String user = "username";
	 * 		String pass = "password";
	 * 		Mockito.when(db.connect(user, pass)).thenReturn(1);
	 * 		Set appController private variables: 
	 * 			username = user;
	 * 			password = pass;
	 * 
	 * Test Input:
	 * 		appController.LogIn();
	 * 
	 * Expected Output: 
	 * 		Variable loggedIn in the appController should return false.
	 */
	@Test
	public void AppCont030()
	{
		//Preconditions
		String user = "username";
		String pass = "password";
		Mockito.when(db.connect(user, pass)).thenReturn(1);
		Whitebox.setInternalState(appController, "username", user);
		Whitebox.setInternalState(appController, "password", pass);

		//TestInput
		appController.LogIn();
		
		//Assertions
		boolean logged = (boolean) Whitebox.getInternalState(appController, "loggedin");
		
		Mockito.verify(db).connect(user, pass);
		
		assertEquals(false, logged);
	}
	
	
	
	/* ID: AppCont031 - appController.java - getTime()
	 * 
	 * Purpose: To test if the getTime() method returns the time set
	 * 		using setTime().
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		setRun state is set to 0 milliseconds time.
	 * 		
	 * Test Input:
	 * 		appController.getTime();
	 * 
	 * Expected Output: 
	 * 		getTime() returns a date of 0.
	 */
	@Test
	public void AppCont031()
	{
		//Preconditions
		Calendar setRun = new GregorianCalendar();
		setRun.setTimeInMillis(0);
		Whitebox.setInternalState(appController, "setRun", setRun);
		
		//Test Input
		Date answer = appController.getTime();
		
		//Assertions
		Date assertDate = setRun.getTime();
		assertEquals(assertDate, answer);
	}
	
	/* ID: AppCont032 - appController.java - getTimeMillis()
	 * 
	 * Purpose: To test that the getTimeMillis() method returns the time
	 * 	in milliseconds from the setRun state variable.
	 * Test Setup: 
	 * 		appController instantiated
	 * 		Set the setRun calendar to 0 milliseconds time.
	 * 
	 * Test Input:
	 * 		appController.getTimeMillis();
	 * 
	 * Expected Output: 
	 * 		getTimeMillis() should return 0.
	 */
	@Test
	public void AppCont032()
	{
		//Preconditions
		Calendar cal = (Calendar) Whitebox.getInternalState(appController, "setRun");
		cal.setTimeInMillis(0);
		Whitebox.setInternalState(appController, "setRun", cal);
		
		//Test Input
		long answer = appController.getTimeMillis();
		
		//Assertions
		assertEquals(0, answer);
	}
	
	/* ID: AppCont033 - appController.java - timerParser()
	 * 
	 * Purpose: To test that the timerParser() method throws and exception
	 * 		when given an invalid value.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		String time = ""
	 * 
	 * Test Input:
	 * 		appController.timerParser(time);
	 * 
	 * Expected Output: 
	 * 		StringIndexOutOfBoundsException thrown.
	 */
	@Test(expected=Exception.class)
	public void AppCont033()
	{
		//Preconditions
		String time = "";
		
		//Test Input
		appController.timerParser(time);
		
		//Assertions
		//Exception should be thrown
		

	}
	
	/* ID: AppCont034 - appController.java - dateParser()
	 * 
	 * Purpose: To test that the dateParser() method parses the inputted date
	 * 		into the respective state variables.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		String date = "12/25/19";
	 * 
	 * Test Input:
	 * 		appController.dateParser(date);
	 * 
	 * Expected Output: 
	 * 		State variables:
	 * 			clearMonth = 12
	 * 			clearDate = 25
	 * 			clearYear = 19
	 */
	@Test(expected = Exception.class)
	public void AppCont034()
	{
		//Preconditions
		String date = "";
		
		//Test Input
		appController.dateParser(date);
		
		//Assertions
		
	}

	/* ID: AppCont035 - appController.java - returnHr()
	 * 
	 * Purpose: To test that returnHr() method returns 0 when not set
	 * 		to any time.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		No time parsing.
	 * 		
	 * Test Input:
	 * 		appController.returnHr()
	 * 
	 * Expected Output: 
	 * 		State variable:
	 * 			hr = 0
	 */
	@Test
	public void AppCont035()
	{
		//Preconditions
		
		//Test Input
		int hr = appController.returnHr();
		
		//Assertions
		assertEquals(0, hr);
	}
	
	/* ID: AppCont036 - appController.java - returnMin()
	 * 
	 * Purpose: To test that the returnMin() method returns 0 when no 
	 * 		time is set.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 
	 * Test Input:
	 * 		appController.returnMin()
	 * 
	 * Expected Output: 
	 * 		State variables:
	 * 			min = 0
	 */
	@Test
	public void AppCont036()
	{
		//Preconditions
		
		//Test Input
		int min = appController.returnMin();
		
		//Assertions
		assertEquals(0, min);
	}
	
	/* ID: AppCont037 - appController.java - getEndTime()
	 * 
	 * Purpose: To test that getEndTime() still returns the correct value when
	 * 		given a time that has it's minutes at 00.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		int hrs = 10;
	 * 		int mins = 0;
	 * 
	 * Test Input:
	 * 		appController.getEndTime(hrs, mins);
	 * 
	 * Expected Output: 
	 * 		When the minute is subtracted by 1 the time should subtract an
	 * 		hour from the hr field and min should go to 59.
	 * 
	 * 		hr = 9;
	 * 		min = 59;
	 */
	@Test
	public void AppCont037()
	{
		//Preconditions
		int hrs = 10;
		int mins = 0;
		
		//Test Input
		Date answer = appController.getEndTime(hrs, mins);
		
		//Assertions
		DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm");
		String date = simple.format(answer).toString();
		
		int hr = Integer.parseInt(date.substring(12, 14));
		int min = Integer.parseInt(date.substring(15, 17));
		
		assertEquals(hrs-1, hr);
		assertEquals(mins+59, min);
	}

	
	/* ID: AppCont038 - appController.java - get15BeforeEnd()
	 * 
	 * Purpose: To test the get15BeforeEnd() method returns todays Date but 15 mins
	 * 		before the specified parameter's time.
	 * 
	 * Test Setup: 
	 * 		appController instantiated
	 * 		int hrs = 10;
	 * 		int mins = 15;
	 * 
	 * Test Input:
	 * 		appController.get15BeforeEnd(hrs, mins);
	 * 		
	 * Expected Output: 
	 * 		The date returned by get15BeforeEnd() should be today's date at
	 * 		9:45.
	 */
	@Test
	public void AppCont038()
	{
		//Preconditions
		int hrs = 10;
		int mins = 0;
		
		//Test Input
		Date answer = appController.get15BeforeEnd(hrs, mins);
		
		//Setting date into simple format to access time
		DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm");
		String date = simple.format(answer).toString();
		
		int hr = Integer.parseInt(date.substring(12, 14));
		int min = Integer.parseInt(date.substring(15, 17));
		
		//Assertions
		assertEquals(hrs-1, hr);
		assertEquals(mins+45, min);
	}
	
	/* ID: AppCont039 - appController.java - get5BeforeEnd()
	 * 
	 * Purpose: To test the get5BeforeEnd() method returns todays Date but 5 mins
	 * 		before the specified parameter's time.
	 * 
	 *Test Setup: 
	 * 		appController instantiated
	 * 		int hrs = 10;
	 * 		int mins = 15;
	 * 
	 * Test Input:
	 * 		appController.get5BeforeEnd(hrs, mins);
	 * 		
	 * Expected Output: 
	 * 		The date returned by get5BeforeEnd() should be today's date at
	 * 		9:55.
	 */
	@Test
	public void AppCont039()
	{
		//Preconditions
		int hrs = 10;
		int mins = 0;
		
		//Test Input
		Date answer = appController.get5BeforeEnd(hrs, mins);
		
		//Setting date into simple format to access time
		DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm");
		String date = simple.format(answer).toString();
		
		int hr = Integer.parseInt(date.substring(12, 14));
		int min = Integer.parseInt(date.substring(15, 17));
		
		//Assertions
		assertEquals(hrs-1, hr);
		assertEquals(mins+55, min);
        
	}
	

	/* ID: AppCont40 - appController.java - main()
	 * 
	 * Purpose: To test the main() method to ensure it executes from the login to logout.
	 * 
	 *Test Setup: 
	 * 		appControllerMain instantiated
	 * 		All dependencies mocked.
	 * 
	 * Test Input:
	 * 		appController.main();
	 * 
	 * Expected Output: 
	 * 		Verify that mocked dependencies were called.
	 */
	//@Test
	public void AppCont40()
	{
		//Precondition
		appControllerMain appControllerMain = new appControllerMain();
		InterfaceController ic = Mockito.mock(InterfaceController.class);
		LoginForm log = Mockito.mock(LoginForm.class);
		Messages msg = Mockito.mock(Messages.class);
		
		ic.msg = msg;
		ic.log = log;
		
		Authenticate auth = Mockito.mock(Authenticate.class);
		
		appControllerMain.ic = ic;
		appControllerMain.auth = auth;
	
		Mockito.doNothing().when(ic).Initiate_Login_Form();
	
		Mockito.when(log.dataReceived()).thenReturn(true);
		
		Mockito.doNothing().when(log).setDataRec(false);
	
		Mockito.when(log.getUsername()).thenReturn("username");
		Mockito.when(log.getPassword()).thenReturn("password");
		
		Mockito.when(auth.validate_Login()).thenReturn(true);
		Mockito.when(auth.logout()).thenReturn(true);
		Mockito.when(db.connect("username", "password")).thenReturn(0);
		
		Mockito.doNothing().when(ic).Initiate_MainMenu();
		
		//checkClear Mock
		Mockito.when(db.getEndDates()).thenReturn(new ArrayList<String>());
		//ac.defTueEnd = "";
		Mockito.doNothing().when(db).clearDatabase();

		//checkTimes Mock
		Mockito.when(db.getCourses()).thenReturn(new ArrayList<Integer>(0));
		
		MainMenu mm = Mockito.mock(MainMenu.class);
		ic.mm = mm;
		Mockito.doNothing().when(mm).setdataRec(false);
		
		Mockito.when(mm.dataRec()).thenReturn(true);
		
		Mockito.when(mm.editSchedSelected()).thenReturn(false);
		Mockito.when(mm.InitSetupSelected()).thenReturn(false);
		Mockito.when(mm.logoutSelected()).thenReturn(true);
		Mockito.doNothing().when(ic).Initiate_Logout();
		
		Mockito.doNothing().when(msg).endClassWarning();
		
		TimerTask eoc = Mockito.mock(TimerTask.class);
		Whitebox.setInternalState(appControllerMain, "endofclass", eoc);
		//Mockito.doNothing().when(ic).msg.endClassWarning();
		//Mockito.doNothing().when(ic.msg).endClassWarning();
		//Test Inputs
		appControllerMain.main(null);
		
		//Assertions
		Mockito.verify(msg).endClassWarning();
		Mockito.verify(ic).Initiate_Login_Form();
	}
	
	

}
