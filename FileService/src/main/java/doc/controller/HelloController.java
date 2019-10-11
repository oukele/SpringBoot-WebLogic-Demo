package doc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/hello")
public class HelloController {

    @GetMapping( path = "/test")
    @ResponseBody
    public String hello(){
        return "hi,word";
    }
}
