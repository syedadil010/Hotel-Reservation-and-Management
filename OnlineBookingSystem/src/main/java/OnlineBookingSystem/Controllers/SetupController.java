package OnlineBookingSystem.Controllers;

import OnlineBookingSystem.ModelClasses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

@Controller
@Service
@RequestMapping(path="/setup")
public class SetupController {

    HttpSession session;
    OBSFascade obs;

    private static Logger logger = Logger.getLogger("SetupController");
    
    @Autowired
    public SetupController(HttpSession session) {
        this.session = session;
        this.obs = OBSModel.getModel();
    }

    @RequestMapping(path="/login", method = RequestMethod.GET)
    public ModelAndView explicitLogin(){
        return new ModelAndView("setup_login");
    }

    @RequestMapping(path="/login", method = RequestMethod.POST)
    public ModelAndView login(
            @RequestParam String authenticationKey
    ){
        if(authenticationKey.equals("1234")) {
            session.setAttribute("authenticated", 1);
            logger.info("Setup user Authenticated");
            return new ModelAndView("setup_account");
        }
        else {
        	ModelAndView mav = new ModelAndView("setup_login");
            return mav.addObject("Error", "Incorrect Authentication key. Please contact OBS for a key.");
        }
    }

    @RequestMapping(path="/account", method = RequestMethod.POST)
    public ModelAndView accountSetup(
            @RequestParam String businessOwnerName,
            @RequestParam String businessName,
            @RequestParam String tagline,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam String businessColor,
            @RequestParam MultipartFile businessLogo,
            @RequestParam MultipartFile businessBanner,
            RedirectAttributes redirectAttrs
    ){
        //check user is logged in
        int authenticated = 0;
        try {
            authenticated = (int) session.getAttribute("authenticated");
        }
        catch (Exception e) {
            logger.fine("Incorrect authenticated value in session. Could not cast.");
        }

        if(authenticated != 1) { //not authenticated
            ModelAndView mav = new ModelAndView("setup_login");
            mav.addObject("User not authenticated.");
            return mav;
        }


        //check input is valid
        //Check first name is alpha only and less then 50 characters long
        if(!businessOwnerName.matches("^[A-Za-z -]+$") || businessOwnerName.length() > 50){
            ModelAndView mav = new ModelAndView("setup_account");
            mav.addObject("Error", "Your name must only contain letters and be 50 or less characters long");
            return mav;
        }

        //Check last name is alpha only and less than 50 characters long
        if(!businessName.matches("^[A-Za-z .',!]+$") || businessName.length() > 50){
            ModelAndView mav = new ModelAndView("setup_account");
            mav.addObject("Error", "Business Name must only contain letters and be 50 or less characters long");
            return mav;
        }

        //Check that phone is of the correct format
        if(!phone.matches("^([+]61([ ]?[0-9]{3}){3})|([0-9]{4}([ ]?[0-9]{3}){2})$")){
            ModelAndView mav = new ModelAndView("setup_account");
            mav.addObject("Error", "Phone must be in the correct format.");
            return mav;
        }

        //Check that address is alpha numeral only
        if(!address.matches("^[a-zA-Z0-9, ]+$")){
            ModelAndView mav = new ModelAndView("setup_account");
            mav.addObject("Error", "Address must contain only letters, numbers and spaces.");
            return mav;
        }


        //Check username is no more than 50 characters long
        if(username.length() > 50){
            ModelAndView mav = new ModelAndView("setup_account");
            mav.addObject("Error", "Username must be lesser than 50 characters");
            return mav;
        }

        //Check username is alpha numeric only
        if(!username.matches("^[0-9A-Za-z]+$")){
            ModelAndView mav = new ModelAndView("setup_account");
            mav.addObject("Error", "Username must only contain letters and numbers");
            return mav;

        }

        //Check for duplicate username
        if(Customer.getByUsername(username) != null){
            ModelAndView mav = new ModelAndView("setup_account");
            mav.addObject("Error", "Username Already Taken");
            return mav;
        }
        if(BusinessOwner.getAllBusinessOwners().size() != 0){
            ModelAndView mav = new ModelAndView("setup_account");
            mav.addObject("Error", "Business owner account already created.");
            return mav;
        }

        //Check password is more than 8 characters long
        if(password.length() < 8){
            ModelAndView mav = new ModelAndView("setup_account");
            mav.addObject("Error", "Password too short");
            return mav;
        }

        //Try to store the file
        try{
            //Get the path
            String userHome = System.getProperty( "user.dir" );

            //File Custome Theme colour
            File theme = new File(userHome + "/theme.css");

            PrintWriter pr = new PrintWriter(theme);
            pr.printf(".theme {\n" +
                    "    background-color: %s; \n" +
                    "}", businessColor);
            pr.close();

            //Check the OBS Directory exists
            File logo = new File(userHome + "/logo.png");
            if(!businessLogo.getContentType().equals("image/png")){
                ModelAndView mav = new ModelAndView("setup_account");
                mav.addObject("Error", "Incorrect Business Logo Format. Picture must be a PNG image.");
                return mav;
            }
            if(!businessBanner.getContentType().equals("image/png")){
                ModelAndView mav = new ModelAndView("setup_account");
                mav.addObject("Error", "Incorrect Business Banner Format. Picture must be a PNG image.");
                return mav;
            }
            businessLogo.transferTo(logo);
            File banner = new File(userHome + "/banner.png");
            businessBanner.transferTo(banner);
        }
        catch (IOException e){
            logger.severe("Unable to store file: " + e.getMessage());
        }

        // insert businessOwner account
        BusinessOwner newBo = new BusinessOwner(
                username, password, businessName, businessOwnerName, address, phone, tagline
        );
        BusinessOwner.save(newBo);

        // insert default shifts
        obs.setUpDefaultShifts();


        //update setup counter
        Setup.setAccountStatus(1);
        session.setAttribute("id", 1);
        session.setAttribute("role", Role.BusinessOwner);
        redirectAttrs.addFlashAttribute("Message", "Welcome to your dashboard, please Click on" +
                " Services to add your first service.");

        return new ModelAndView("redirect:/businessowner/dashboard");
    }


    @RequestMapping(path="/edit", method = RequestMethod.POST)
    public ModelAndView accountEdit(
            @RequestParam String businessOwnerName,
            @RequestParam String businessName,
            @RequestParam String tagline,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam String businessColor,
            @RequestParam MultipartFile businessLogo,
            @RequestParam MultipartFile businessBanner,
            RedirectAttributes redirectAttrs
    ){
        //Check login
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }

        //check input is valid
        //Check first name is alpha only and less then 50 characters long
        if(!businessOwnerName.matches("^[A-Za-z -]+$") || businessOwnerName.length() > 50){
            ModelAndView mav = new ModelAndView("setup_account");
            mav.addObject("Error", "Your name must only contain letters and be 50 or less characters long");
            return mav;
        }

        //Check last name is alpha only and less than 50 characters long
        if(!businessName.matches("^[A-Za-z .',!]+$") || businessName.length() > 50){
            ModelAndView mav = new ModelAndView("setup_account");
            mav.addObject("Error", "Business Name must only contain letters and be 50 or less characters long");
            return mav;
        }

        //Check that phone is of the correct format
        if(!phone.matches("^([+]61([ ]?[0-9]{3}){3})|([0-9]{4}([ ]?[0-9]{3}){2})$")){
            ModelAndView mav = new ModelAndView("setup_account");
            mav.addObject("Error", "Phone must be in the correct format.");
            return mav;
        }

        //Check that address is alpha numeral only
        if(!address.matches("^[a-zA-Z0-9, ]+$")){
            ModelAndView mav = new ModelAndView("setup_account");
            mav.addObject("Error", "Address must contain only letters, numbers and spaces.");
            return mav;
        }
        //Try to store the file
        try{
            //Get the path
            String userHome = System.getProperty( "user.dir" );

            //File Custome Theme colour
            File theme = new File(userHome + "/theme.css");

            PrintWriter pr = new PrintWriter(theme);
            pr.printf(".theme {\n" +
                    "    background-color: %s; \n" +
                    "}", businessColor);
            pr.close();

            //Check the OBS Directory exists
            File logo = new File(userHome + "/logo.png");
            if(!businessLogo.getContentType().equals("image/png")){
                ModelAndView mav = new ModelAndView("setup_account");
                mav.addObject("Error", "Incorrect Business Logo Format. Picture must be a PNG image.");
                return mav;
            }
            if(!businessBanner.getContentType().equals("image/png")){
                ModelAndView mav = new ModelAndView("setup_account");
                mav.addObject("Error", "Incorrect Business Banner Format. Picture must be a PNG image.");
                return mav;
            }
            businessLogo.transferTo(logo);
            File banner = new File(userHome + "/banner.png");
            businessBanner.transferTo(banner);
        }
        catch (IOException e){
            logger.severe("Unable to store file: " + e.getMessage());
        }

        BusinessOwner.update(businessName, businessOwnerName, address, phone, tagline);

        ModelAndView mav = new ModelAndView("redirect:/businessowner/dashboard");

        return mav;
    }
    @RequestMapping(path="/edit", method = RequestMethod.GET)
    public ModelAndView edit(){
        //Check login
        User user = LoginController.checkLogin(session);
        if(user == null || !(user instanceof BusinessOwner)){
            session.invalidate();
            return new ModelAndView("redirect:/");
        }

        BusinessOwner bo = obs.getAllBusinessOwners().get(0);
        ModelAndView mav = new ModelAndView("editbusiness");
        mav.addObject("bo", bo);
        return mav;
    }
}
