package com.fakeco.instafake.controllers;

import com.fakeco.instafake.dto.request.AuthenticationRequest;
import com.fakeco.instafake.dto.request.FollowRequest;
import com.fakeco.instafake.dto.response.AuthenticationResponse;
import com.fakeco.instafake.dto.response.Metadata;
import com.fakeco.instafake.dto.response.UserDTOResponse;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.services.FollowService;
import com.fakeco.instafake.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Metadata> register(@RequestBody UserModel user) throws Exception {
        userService.register(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Metadata> login(@RequestBody AuthenticationRequest user) throws Exception {
        Metadata metadata = userService.authenticate(user);
        System.out.println(metadata.toString());
        return ResponseEntity.ok(metadata);
    }

}
