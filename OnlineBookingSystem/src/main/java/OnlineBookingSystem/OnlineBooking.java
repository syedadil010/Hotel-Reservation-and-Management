package OnlineBookingSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.xml.crypto.Data;
import java.util.logging.Logger;

/**
 * Online Booking system, Main Application entry point.
 */
@SpringBootApplication
public class OnlineBooking {

	private static final Logger logger = Logger.getLogger("OnlineBooking");
	public static void main(String[] args) {
		SpringApplication.run(OnlineBooking.class, args);
	}
}