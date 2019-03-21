package Default;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import my.PSM.DBConnection;

@RunWith(Suite.class)
@SuiteClasses({appControllerTest.class, DBConnectionTest.class})
public class MasterDriver
{
	
}