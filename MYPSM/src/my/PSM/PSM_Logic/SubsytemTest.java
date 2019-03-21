package my.PSM.PSM_Logic;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import my.PSM.PSM_Storage.*;
import org.mockito.*;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;

//import Default.InterfaceController;
import my.PSM.PSM_Interface.*;

//@RunWith(PowerMockRunner.class);

public class SubsytemTest {
	
	appController app;
	@Mock 
	DBConnection db = Mockito.mock(DBConnection.class);
	@Mock
	InterfaceController ic = Mockito.mock(InterfaceController.class);
	@Mock
	LoginForm log = Mockito.mock(LoginForm.class);
	@Mock
	MainMenu mm = Mockito.mock(MainMenu.class);
	@Mock
	courseSelect cs = Mockito.mock(courseSelect.class);
	@Mock
	ScheduleForm sched = Mockito.mock(ScheduleForm.class);

	@Before
	public void setUp() throws Exception {
		app = new appController();
		app.db = db;
		ic.log = log;
		ic.mm = mm;
		ic.cs = cs;
		ic.sched = sched;
	}

	/* ID: SubSys01
	 * 
	 * Purpose: Test the call to the method LoginForm.dataReceived
	 * 			returns true.
	 * 
	 * Test Setup: Mockito.when(ic.log.dataReceived()).thenReturn(true);
	 * 			   sets a boolean variable to the returned boolean value.
	 * 
	 * Test Input: InterfaceController.LoginForm.dataReceived();
	 * 
	 * Test Output: True
	 */
	@Test
	public void SubSys01() {
		Mockito.when(ic.log.dataReceived()).thenReturn(true);
		boolean bool = ic.log.dataReceived();
		assertTrue(bool);	
		Mockito.verify(ic.log).dataReceived();
	}
	
	/* ID: SubSys02
	 * 
	 * Purpose: Test the call to the method LoginForm.dataReceived
	 * 			returns false.
	 * 
	 * Test Setup: Mockito.when(ic.log.dataReceived()).thenReturn(false);
	 * 			   sets a boolean variable to the returned boolean value.
	 * 
	 * Test Input: InterfaceController.LoginForm.dataReceived();
	 * 
	 * Test Output: false
	 */
	@Test
	public void SubSys02() {
		Mockito.when(ic.log.dataReceived()).thenReturn(false);
		boolean bool = ic.log.dataReceived();
		assertFalse(bool);	
		Mockito.verify(ic.log).dataReceived();
	}
	
	/* ID: SubSys03
	 * 
	 * Purpose: Test the call to LoginForm.setDataRec(true);
	 * 			sets a private instance variable dataRec to true value.
	 * 
	 * Test Setup: InterfaceController.LoginForm();
	 * 			   ic.log.setDataRec(true);
	 * 
	 * Test Input: ic.log.setDataRec(true);
	 * 
	 * Test Output: dataRec = true;
	 */
	@Test
	public void SubSys03() {
		
		ic.log = new LoginForm();
		ic.log.setDataRec(true);
		boolean bool = (boolean) Whitebox.getInternalState(ic.log, "dataRec");
		assertTrue(bool);
	}
	
	/* ID: SubSys04
	 * 
	 * Purpose: Test the call to LoginForm.setDataRec(false);
	 * 			sets a private instance variable dataRec to false value.
	 * 
	 * Test Setup: InterfaceController.LoginForm();
	 * 
	 * Test Input: ic.log.setDataRec(false);
	 * 
	 * Test Output: dataRec = false;
	 */
	@Test
	public void SubSys04() {
		
		ic.log = new LoginForm();
		ic.log.setDataRec(false);
		boolean bool = (boolean) Whitebox.getInternalState(ic.log, "dataRec");
		assertFalse(bool);
	}
	
	/* ID: SubSys05
	 * 
	 * Purpose: To test the call to LoginForm.getUsername() and LoginForm.getPassword()
	 * 			returns a string in both cases correctly.
	 * 
	 * Test Setup: Mockito.when(ic.log.getUsername()).thenReturn("root");
	 * 			   Mockito.when(ic.log.getPassword()).thenReturn("pass1234");
	 * 
	 * Test Input: String user = ic.log.getUsername();
	 * 			   String pass = ic.log.getPassword();
	 * 
	 * Test Output: user = "root"
	 * 				pass = "pass1234"
	 * 
	 */
	@Test
	public void SubSys05() {
		
		Mockito.when(ic.log.getUsername()).thenReturn("root");
		String user = ic.log.getUsername();
		assertEquals("root", user);
		Mockito.verify(ic.log).getUsername();
		
		Mockito.when(ic.log.getPassword()).thenReturn("pass1234");
		String pass = ic.log.getPassword();
		assertEquals("pass1234", pass);
		Mockito.verify(ic.log).getPassword();
	}
	
	/* ID: SubSys06
	 * 
	 * Purpose: To test the call to LoginForm.getUsername() and LoginForm.getPassword()
	 * 			returns a string in both cases correctly.
	 * 
	 * Test Setup: Mockito.when(ic.log.getUsername()).thenReturn("");
	 * 			   Mockito.when(ic.log.getPassword()).thenReturn("");
	 * 
	 * Test Input: String user = ic.log.getUsername();
	 * 			   String pass = ic.log.getPassword();
	 * 
	 * Test Output: user = ""
	 * 				pass = ""
	 * 
	 */
	@Test
	public void SubSys06() {
		
		Mockito.when(ic.log.getUsername()).thenReturn("");
		String user = ic.log.getUsername();
		assertEquals("", user);
		
		Mockito.when(ic.log.getPassword()).thenReturn("");
		String pass = ic.log.getPassword();
		assertEquals("", pass);
	}
	
	/* ID: SubSys07
	 * 
	 */
	@Test
	public void SubSys07() {
		ic.msg.ack = true;
		boolean dataReceived = ic.msg.ack;
		assertTrue(dataReceived);
	}
	
	/* ID: SubSys
	 * 
	 */
	@Test
	public void SubSys08() {
		Mockito.when(ic.mm.dataRec()).thenReturn(true);
		boolean dataRec = ic.mm.dataRec();
		assertTrue(dataRec);
		Mockito.verify(ic.mm).dataRec();
	}
	
	/* ID: SubSys
	 * 
	 */
	@Test
	public void SubSys09() {
		Mockito.when(ic.mm.editSchedSelected()).thenReturn(true);
		boolean edScheSel = ic.mm.editSchedSelected();
		assertTrue(edScheSel);
		Mockito.verify(ic.mm).editSchedSelected();
	}
	
	/* ID: SubSys
	 * 
	 */
	@Test
	public void SubSys10() {
		Mockito.when(ic.mm.InitSetupSelected()).thenReturn(true);
		boolean schedSetupSel = ic.mm.InitSetupSelected();
		assertTrue(schedSetupSel);
		Mockito.verify(ic.mm).InitSetupSelected();
	}
	
	/* ID: SubSys
	 * 
	 */
	@Test
	public void SubSys11() {
		Mockito.when(ic.mm.logoutSelected()).thenReturn(true);
		boolean logoutSel = ic.mm.logoutSelected();
		assertTrue(logoutSel);
		Mockito.verify(ic.mm).logoutSelected();
	}
	
	/* ID: SubSys
	 * 
	 */
	@Test
	public void SubSys12() {
		ic.mm = new MainMenu();
		ic.mm.setdataRec(true);
		boolean bool = (boolean) Whitebox.getInternalState(ic.mm, "dataRec");
		assertTrue(bool);
	}
	
	/* ID: SubSys
	 * 
	 */
	@Test
	public void SubSys13() {
		Mockito.when(ic.cs.courseSelected()).thenReturn(true);
		boolean bool = ic.cs.courseSelected();
		assertTrue(bool);
		Mockito.verify(ic.cs).courseSelected();
	}
	
	/* ID: SubSys
	 * 
	 *
	@Test
	public void SubSys21() {
		//ic.cs = new courseSelect();
        ic.cs.setCourseSelected(true);
        boolean bool = (boolean) Whitebox.getInternalState(ic.cs, "courseSelected");
        assertTrue(bool);
		
	}*/
	
	/* ID: SubSys
	 * 
	 */
	@Test
	public void SubSys14() {
		Mockito.when(ic.cs.getSelection()).thenReturn(2);
		int num = ic.cs.getSelection();
		assertEquals(2, num);
	}
	
	/* ID: SubSys
	 * 
	 */
	@Test
	public void SubSys15() {
		
		Mockito.when(ic.sched.dataRec()).thenReturn(true);
		boolean bool = ic.sched.dataRec();
		assertTrue(bool);
	}
	
	/* ID: SubSys
	 * 
	 
	@Test
	public void SubSys27() {
		ic.edSched.setDataRec(false);
	}
	*/
	/* ID: SubSys
	 * 
	 
	@Test
	public void SubSys29() {
		ic.edSched.defCourseID = 4072;
		ic.edSched.newCourseStart = "01/10/2019";
		ic.edSched.newCourseEnd = "04/29/2019"; 
        ic.edSched.newMonStart = "";
        ic.edSched.newMonEnd = "";
        ic.edSched.newTueStart = "18:25";
        ic.edSched.newTueEnd = "19:40";
        ic.edSched.newWedStart = "";
        ic.edSched.newWedEnd = "";
        ic.edSched.newThuStart = "18:25";
        ic.edSched.newThuEnd = "19:40";
        ic.edSched.newFriStart = "";
        ic.edSched.newFriEnd = "";
        ic.edSched.newSatStart = "";
        ic.edSched.newSatEnd = "";
        
        
        
	}
	
	ID: SubSys
	 * 
	 
	@Test
	public void SubSys16() {
		Mockito.doNothing().when(ic.sched).launchInitial();
		Mockito.verify(ic.sched).launchInitial();
	}
	*/
	/* ID: SubSys33
	 * 
	 * Purpose: To test that the call to DBConnection.fetchCourses()
	 * 			, when called would return an empty string
	 * 
	 * Test Setup: Mockito.when(db.fetchCourses()).thenReturn("");
	 * 
	 * Test Input: String ans = app.db.fetchCourses();
	 * 
	 * Test Output: ans = "";
	 * 
	 */
	@Test
	public void SubSys33() {
		Mockito.when(db.fetchCourses()).thenReturn("");
		String ans = app.db.fetchCourses();
		assertEquals("", ans);
		
		Mockito.verify(db).fetchCourses();
	}
	
	/* ID: SubSys34
	 * 
	 * Purpose: To test that the call to DBConnection.fetchCourses()
	 * 			, when called would return an empty string
	 * 
	 * Test Setup: Mockito.when(db.fetchCourses()).thenReturn("CEN4072,COP3337,CDA4101,STA4106,COP2401");
	 * 
	 * Test Input: String ans = app.db.fetchCourses();
	 * 
	 * Test Output: ans = "CEN4072,COP3337,CDA4101,STA4106,COP2401";
	 * 
	 */
	@Test
	public void SubSys34() {
		Mockito.when(db.fetchCourses()).thenReturn("CEN4072,COP3337,CDA4101,STA4106,COP2401");
		String ans = app.db.fetchCourses();
		assertEquals("CEN4072,COP3337,CDA4101,STA4106,COP2401", ans);
		Mockito.verify(db).fetchCourses();
	}
	

}
