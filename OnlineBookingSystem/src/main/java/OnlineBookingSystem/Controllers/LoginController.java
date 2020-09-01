package OnlineBookingSystem.Controllers;

import OnlineBookingSystem.ModelClasses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping(path="/")
@Service
public class LoginController {

    private final HttpSession session;
    private static Logger logger = Logger.getLogger("Login Controller");
    private OBSFascade obs;

    @Autowired
    public LoginController(HttpSession session) {
        this.session = session;
        this.obs = OBSModel.getModel();
    }

    @RequestMapping(path="/login", method = RequestMethod.GET)
    public ModelAndView explicitLogin(){
        return new ModelAndView("login");
    }

    @RequestMapping(path="/", method = RequestMethod.GET)
    public ModelAndView login(){
        //check if business account is setup
        if(obs.getAccountStatus() == 0) {
            logger.info("BO Account not set up");
            return new ModelAndView("redirect:/setup/login");
        }
        else {
            ModelAndView mav = new ModelAndView("landing");
            List<BusinessOwner> bo = obs.getAllBusinessOwners();
            mav.addObject("ownerList", bo);
            return mav;
        }
    }

    @RequestMapping(path="/", method = RequestMethod.POST)
    public ModelAndView login(
            @RequestParam String username,
            @RequestParam String password
    ){
        //Check
        Customer cust = obs.getCustomerByUsername(password);
        BusinessOwner bo = obs.getBusinessOwnerByUsername(password);
        if(cust != null){
            //Handle Customer login
            if(!password.equals(cust.getPassword())){
                return incorretPassword();
            }

            //Successfully authenticated as Customer
            //Set session attribute
            session.setAttribute("id",cust.getId());
            session.setAttribute("role", Role.Customer);

            //Redirect to dashboard
            return new ModelAndView("redirect:/customer/dashboard");
        }
        //Check for Business Partner and Handle it
        else if(bo != null){
            if(!password.equals(bo.getPassword())){
                return incorretPassword();
            }
            //Successfully authenticated as Customer
            //Set session attribute
            session.setAttribute("id",bo.getId());
            session.setAttribute("role", Role.BusinessOwner);
            return new ModelAndView("redirect:/businessowner/dashboard");
        }
        else{
            //Must be incorrect username
            return incorretPassword();
        }
    }

    private ModelAndView incorretPassword(){
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("Error", "Incorrect username or password");
        return mav;
    }
    
    @RequestMapping(path="/logout")
    public ModelAndView logout() {
    	ModelAndView mav = new ModelAndView("redirect:/");
    	session.invalidate();
    	return mav;
    } 


    //Utility function to get the user thats currently logged in.
    static User checkLogin(HttpSession session){
        OBSFascade obs = OBSModel.getModel();
        Role role = (Role)session.getAttribute("role");
        if(role == null){
            return null;
        }
        User user = null;
        if(role == Role.Customer){
            user = obs.getCustomerById((Integer)session.getAttribute("id"));
        }
        else if(role == Role.BusinessOwner){
            user = obs.getBusinessOwnerById((Integer)session.getAttribute("id"));
        }
        return user;
    }

    @RequestMapping(path="/dashboard")
    public ModelAndView dashboard() {
        //this method is used to redirect the user depending on their role
        //Then the dashboard resource can be called at anytime regardless of who is logged in
        User user = LoginController.checkLogin(session);
        if (user instanceof BusinessOwner) {
            return new ModelAndView("redirect:/businessowner/dashboard");
        }
        else if (user instanceof Customer) {
            return new ModelAndView("redirect:/customer/dashboard");
        }
        else {
            session.invalidate();
            return new ModelAndView("redirect:/");
        }
    }
}
