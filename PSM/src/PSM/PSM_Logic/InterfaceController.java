package PSM.PSM_Logic;

import my.PSM.LogOutConfirm;
import my.PSM.LoginForm;
import my.PSM.MainMenu;
import my.PSM.Messages;
import my.PSM.PrefilledScheduleForm;
import my.PSM.ScheduleForm;
import my.PSM.courseSelect;

//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : PSM - Professor Schedule Manager
//  @ File Name : 
//  @ Date : 03/16/2008
//  @ Author : 
//
//

class InterfaceController {
    
    public Messages msg = new Messages();
    
    public LoginForm log = new LoginForm();
    public ScheduleForm sched = new ScheduleForm();
    public PrefilledScheduleForm edSched = new PrefilledScheduleForm();
    public MainMenu mm = new MainMenu();
    public LogOutConfirm logout = new LogOutConfirm();
    public courseSelect cs;
    
    public InterfaceController()
    {
        
    }
    
    public void passwordLock()
    {
        msg.lockedOut();
    }
    public void Initiate_Logout() 
    {
        //System.out.print("lame");
        
         //logout.launchLogout();
         msg.logoutConfirmation();
     
    }

    public void Initiate_IncorrectLogin()
    {
        
        msg.incorrectLogin();
        //il.launchForm();
        
    }
    
    public void Initiate_Schedule_Form() 
    {
       
       sched.launchInitial();
    }

    public void Pre_Filled_Form(int courseID, String courseSubj, String courseName, String semester,
            String startDate, String endDate, String startMon, String endMon,
            String startTue, String endTue, String startWed, String endWed, String startThu, String endThu, 
            String startFri, String endFri, String startSat, String endSat) 
    {

    
        
         edSched.launchEdit(courseID, courseSubj, courseName, semester,
            startDate, endDate, startMon, endMon, startTue, endTue, 
            startWed, endWed, startThu, endThu, startFri, endFri, startSat, endSat);
    }
    
    public void Course_Select_Form(){
        cs = new courseSelect();
        cs.launchCourse();
    }
    public void Initiate_Login_Form() {

        log.launchForm();

    }

    public void Initiate_Message() {

    }

    public void Initiate_MainMenu() {
        mm.launchForm();

    }
}
