package com.fakeco.instafake.controllers;

import com.fakeco.instafake.dto.AuthenticationResponse;
import com.fakeco.instafake.dto.Metadata;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody UserModel user) throws Exception {
        userService.register(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody UserModel user) throws Exception {
        return ResponseEntity.ok(userService.authenticate(user));
    }


}
