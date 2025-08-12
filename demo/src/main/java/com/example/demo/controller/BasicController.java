package com.example.demo.controller;


//import com.example.demo.exception.ErrorCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BasicController {
    public BasicController() {
    }

    @RequestMapping({"/hello"})
    @ResponseBody
    public String hello(@RequestParam(name = "name",defaultValue = "unknown user") String name) {
        System.out.println(1 / 0);
        return "Hello " + name;
    }

    @RequestMapping({"/user"})
    @ResponseBody
    public User user() {
   //     ErrorCode errorCode = ErrorCode.FORBIDDEN_ERROR;
        User user = new User();
        user.setName("theonefx");
        user.setAge(666);
        return user;
    }

    @RequestMapping({"/save_user"})
    @ResponseBody
    public String saveUser(User u) {
        String var10000 = u.getName();
        return "user will save: name=" + var10000 + ", age=" + u.getAge();
    }

    @RequestMapping({"/html"})
    public String html() {
        return "index.html";
    }

    @ModelAttribute
    public void parseUser(@RequestParam(name = "name",defaultValue = "unknown user") String name, @RequestParam(name = "age",defaultValue = "12") Integer age, User user) {
        user.setName("zhangsan");
        user.setAge(18);
    }
}
