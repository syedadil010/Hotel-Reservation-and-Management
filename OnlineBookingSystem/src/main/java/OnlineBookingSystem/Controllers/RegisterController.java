package OnlineBookingSystem.Controllers;

import OnlineBookingSystem.ModelClasses.BusinessOwner;
import OnlineBookingSystem.ModelClasses.Customer;
import OnlineBookingSystem.ModelClasses.OBSFascade;
import OnlineBookingSystem.ModelClasses.OBSModel;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Service
@RequestMapping(path = "/register")
public class RegisterController {

    private OBSFascade obs;

    public RegisterController(){
        obs = OBSModel.getModel();
    }

    @RequestMapping(path="/customer", method= RequestMethod.GET)
    public ModelAndView registerForm() {
        ModelAndView mav = new ModelAndView("customerregistration");
        mav.addObject("title", "OBS | Customer Registration");
        return mav;
    }

    @RequestMapping(path="/customer", method = RequestMethod.POST)
    public ModelAndView register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirm,
            @RequestParam String email,
            @RequestParam String firstName,
            @RequestParam String lastName,
            RedirectAttributes redirectAttrs
    ){
        //Check first name is alpha only and less then 50 characters long
        if(!firstName.matches("^[A-Za-z]+$") || firstName.length() > 50){
            ModelAndView mav = new ModelAndView("customerregistration");
            mav.addObject("Error", "First Name must only contain letters and be 50 or less characters long");
            return mav;
        }

        //Check last name is alpha only and less than 50 characters long
        if(!lastName.matches("^[A-Za-z]+$") || lastName.length() > 50){
            ModelAndView mav = new ModelAndView("customerregistration");
            mav.addObject("Error", "Last Name must only contain letters and be 50 or less characters long");
            return mav;
        }

        //Check email is letter/number followed by @ followed by letter/number
        if(!email.matches("^[0-9A-Za-z.+]+@[0-9A-Za-z]+[.][0-9A-Za-z.]+$")){
            ModelAndView mav = new ModelAndView("customerregistration");
            mav.addObject("Error", "Please enter valid email");
            return mav;
        }

        //Check username is no more than 50 characters long
        if(username.length() > 50){
            ModelAndView mav = new ModelAndView("customerregistration");
            mav.addObject("Error", "Username must be lesser than 50 characters");
            return mav;

        }
        //Check username is alpha numeric only
        if(!username.matches("^[0-9A-Za-z]+$")){
            ModelAndView mav = new ModelAndView("customerregistration");
            mav.addObject("Error", "Username must only contain letters and numbers");
            return mav;

        }

        //Check for duplicate username
        if(Customer.getByUsername(username) != null){
            ModelAndView mav = new ModelAndView("customerregistration");
            mav.addObject("Error", "Username Already Taken");
            return mav;
        }
        if(Customer.getByUsername(username) != null){
            ModelAndView mav = new ModelAndView("customerregistration");
            mav.addObject("Error", "Username Already Taken");
            return mav;
        }

        //Check password is more than 8 characters long
        if(password.length() < 8){
            ModelAndView mav = new ModelAndView("customerregistration");
            mav.addObject("Error", "Password too short");
            return mav;
        }

        //Check the password and confirm match
        if(!password.equals(confirm)){
            ModelAndView mav = new ModelAndView("customerregistration");
            mav.addObject("Error", "Password don't match");
            return mav;
        }


        //Create and save new customer
        Customer newCustomer = new Customer(
                firstName, lastName, email, username, password
        );
        obs.saveCustomer(newCustomer);

        //Render the Login page with a message of Successful registration
        ModelAndView mav = new ModelAndView("redirect:/login");
        redirectAttrs.addFlashAttribute("Message", "Registration Successful");
        return mav;
    }

}
