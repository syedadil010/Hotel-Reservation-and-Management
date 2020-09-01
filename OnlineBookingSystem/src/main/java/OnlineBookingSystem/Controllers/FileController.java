package OnlineBookingSystem.Controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

@Controller
@RequestMapping(path="/file/")
@Service
public class FileController {
    private static Logger logger = Logger.getLogger("File Controller");

    //The following methods represent resource locations where the uploaded files will be stored
    //The "user.dir" is the project root in the file system

    @RequestMapping(path="/logo.png", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getLogo() {
        String userHome = System.getProperty( "user.dir" );
        return new FileSystemResource(new File(userHome + "/logo.png"));
    }

    @RequestMapping(path="/banner.png", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getBanner() {
        String userHome = System.getProperty( "user.dir" );
        return new FileSystemResource(new File(userHome + "/banner.png"));
    }
    @RequestMapping(path="/theme.css", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getTheme() {
        String userHome = System.getProperty( "user.dir" );
        return new FileSystemResource(new File(userHome + "/theme.css"));
    }
}
