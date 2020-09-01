package OnlineBookingSystem.DisplayClasses;

import OnlineBookingSystem.ModelClasses.Day;

import java.util.ArrayList;


public class ShiftCell extends TableCell {
    public ShiftStatus status;
    public ArrayList<Link> employees;
    public Day day;
}
