package OnlineBookingSystem.DisplayClasses;

import OnlineBookingSystem.DisplayClasses.TableCell;

import java.util.ArrayList;


public class TableRow {

    public TableRow(){
        this.headers = new ArrayList<>();
        this.cells = new ArrayList<>();
    }
    //used for passing identification information for a row.
    public String id;
    //used for rendering the top row of the table
    public ArrayList<TableCell> headers;
    //Used as the value for the first cell of the table
    public String heading;
    //Collection for each cell in the table
    public ArrayList<TableCell> cells;
}
