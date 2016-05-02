/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import java.util.Date;
import org.primefaces.model.DefaultScheduleEvent;

/**
 *
 * @author AndresAngel
 */
public class Event extends DefaultScheduleEvent{
    
    
    @Override
    public void setStartDate(Date d){
        super.setStartDate(d);
    }
    
    @Override
    public void setEndDate(Date d){
        super.setStartDate(d);
    }
}
