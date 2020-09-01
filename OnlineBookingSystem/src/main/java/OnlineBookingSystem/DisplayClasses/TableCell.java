package OnlineBookingSystem.DisplayClasses;


public class TableCell {
    public String text;
    public String displayClass;
    public String columnName; //columnName is used for when the table collapses on a small screen
    public boolean hasValue; //used for marking the cell

    public TableCell() {
        //empty constructor
    }

    public TableCell(String text) {
        this.text = text;
    }

    public TableCell(String text, String columnName) {
        this.text = text;
        this.columnName = columnName;
    }

    public TableCell(boolean value) {
        this.hasValue = value;
    }
}
