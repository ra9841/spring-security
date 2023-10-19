package com.rabin.securityproject.controller;

import com.rabin.securityproject.dto.AuthenticateRequest;
import com.rabin.securityproject.dto.AuthenticateResponse;
import com.rabin.securityproject.dto.UserDto;
import com.rabin.securityproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/security")
public class UserController {

    @Autowired
    private UserService userService;


    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public AuthenticateResponse savingTheRecords(@RequestBody UserDto userDto) throws Exception {
        return userService.registrationOfUserInfo(userDto);  //response is token
    }


    @PostMapping("/authenticate")
    public AuthenticateResponse authenticate(@RequestBody AuthenticateRequest request) {
        return userService.authenticate(request);
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome to java class";
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDto> getAllRecord() {
        return userService.getAllRecord();
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('USER')")
    public UserDto getRecordByUsername(@PathVariable String username) {
        return userService.getRecordByUsername(username);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteRecord(@PathVariable String username) throws Exception {
        return userService.deleteRecord(username);
    }

    @PostMapping("/update/{username}")
    @PreAuthorize("hasAuthority('USER')")
    public UserDto updatingUserInfo(@RequestBody UserDto userDto,@PathVariable String username) throws Exception {
       return userService.updatinguserInforamtion(userDto,username);
    }

}
