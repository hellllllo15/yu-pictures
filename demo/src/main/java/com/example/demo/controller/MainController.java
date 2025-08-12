package com.example.demo.controller;


import com.example.demo.common.BaseResponse;
import com.example.demo.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
public class MainController {
@GetMapping("/health")
   public BaseResponse<String> health  (){
return ResultUtils.success("ok");
    }
}
