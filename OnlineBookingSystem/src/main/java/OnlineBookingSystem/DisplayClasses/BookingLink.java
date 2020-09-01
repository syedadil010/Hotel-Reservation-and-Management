package OnlineBookingSystem.DisplayClasses;


public class BookingLink {
    public String text;
    public String href;
    public int numBookings;
    public String cssClass;

    public BookingLink(String text, String href, int numBookings){
        this.text = text;
        this.href = href;
        this.numBookings = numBookings;
        determineClass(this.numBookings);
    }
    
    private void determineClass(int numBookings) {
    	if(numBookings > 0) {
    		this.cssClass = "btn btn-success";
    	} else {
    		this.cssClass = "btn btn-default";
    	}
    }
}
