package OnlineBookingSystem.DisplayClasses;


public class Link {
    public String text;
    public String href;
    public int numBookings;
    public String cssClass;

    public Link(String text, String href, int numBookings){
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
