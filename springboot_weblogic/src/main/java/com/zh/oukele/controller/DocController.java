package com.zh.oukele.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@CrossOrigin
@RequestMapping( path = "/oukele")
public class DocController {

    @GetMapping
    @ResponseBody
    public String index(){
        return "hello world !";
    }

    @GetMapping("/getIndex")
    public String testCent() {
        return "index";
    }

}
