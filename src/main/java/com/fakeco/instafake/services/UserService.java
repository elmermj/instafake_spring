package com.fakeco.instafake.services;

import com.fakeco.instafake.dto.AuthenticationResponse;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.models.enums.Roles;
import com.fakeco.instafake.repos.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.util.Collection;
import java.util.Objects;


@Service
public class UserService implements UserDetails {

    Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(UserModel request){
        UserModel user = new UserModel();
        user.setRealname(request.getRealname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());

        user.setRole(request.getRole());

        user = userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse("Bearer "+token);

    }

    public AuthenticationResponse authenticate(UserModel request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserModel user = userRepository.findByUsername(request.getUsername());

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public UserModel getUserFromToken(String token) {
        try {
            String username = jwtService.extractUsername(token);

            return userRepository.findByUsername(username);
        } catch (Exception e) {
            // Token is invalid or expired
            return null;
        }
    }


    //CREATE
    public void registerUser(UserModel user) throws Exception {
        // Check if username or email already exists
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(user.getPassword());
        user.setRole(Roles.USER);

        System.out.println(user.getRole());

        log.debug(user.getEmail() + " ::::::: " +user.getCreatedAt());

        userRepository.save(user);
    }


    //READ
    public UserModel findByUsername(String username) throws Exception {
        System.out.println("TEST LOGIN SENDING USERNAME ::: "+username);
        UserModel user = userRepository.findByUsername(username);
        System.out.println("TEST LOGIN RETURN USERNAME ::: "+user.getUsername());

        return user;
    }

    public boolean validateToken(String token) {
        try {
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("123456789");
//            Jwts.parser().setSigningKey(new String(apiKeySecretBytes)).parseClaimsJwt(token);
            return true;
        } catch (Exception e) {
            // Token validation failed
            return false;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

}
