package com.fakeco.instafake.controllers;

import com.fakeco.instafake.dto.request.AuthenticationRequest;
import com.fakeco.instafake.dto.request.FollowRequest;
import com.fakeco.instafake.dto.response.AuthenticationResponse;
import com.fakeco.instafake.dto.response.Metadata;
import com.fakeco.instafake.dto.response.UserDTOResponse;
import com.fakeco.instafake.exceptions.InvalidCredentialsException;
import com.fakeco.instafake.exceptions.UserException;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.services.FollowService;
import com.fakeco.instafake.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserModel user) {
        try {
            Metadata metadata = userService.register(user);
            return new ResponseEntity<>(metadata, HttpStatus.CREATED);
        } catch (UserException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest user) {
        try {
            Metadata metadata = userService.authenticate(user);
            System.out.println(metadata.toString());
            return ResponseEntity.ok(metadata);
        } catch (InvalidCredentialsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/search/{query}")
    public ResponseEntity<List<UserDTOResponse>> searchUser(@PathVariable String query) throws Exception {
        List<UserDTOResponse> users = userService.searchUser(query);
        System.out.println(users.toString());
        return ResponseEntity.ok(users);
    }

}
