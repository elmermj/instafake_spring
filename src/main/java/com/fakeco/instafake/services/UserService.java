package com.fakeco.instafake.services;

import com.fakeco.instafake.dto.request.AuthenticationRequest;
import com.fakeco.instafake.dto.response.AuthenticationResponse;
import com.fakeco.instafake.dto.response.Metadata;
import com.fakeco.instafake.dto.response.UserDTOResponse;
import com.fakeco.instafake.exceptions.InvalidCredentialsException;
import com.fakeco.instafake.exceptions.UserException;
import com.fakeco.instafake.models.UserModel;
import com.fakeco.instafake.repos.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService implements UserDetails {

    Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public Metadata register(UserModel request) throws UserException {
        try {
            UserModel user = new UserModel();
            user.setRealname(request.getRealname());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setUsername(request.getUsername());

            user.setRole(request.getRole());

            user = userRepository.save(user);

            String token = jwtService.generateToken(user);

            return new Metadata("Bearer " + token, user);
        } catch (DataIntegrityViolationException e) {
            throw new UserException("Username or Email already exists");
        }
    }

    public List<UserDTOResponse> searchUser(String query){
        String searchQuery = "%" + query + "%";
        return userRepository.findByQuery(searchQuery).stream().map(UserDTOResponse::new).collect(Collectors.toList());
    }

    public Metadata authenticate(AuthenticationRequest request) throws InvalidCredentialsException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserModel user = userRepository.findByUsername(request.getUsername());
            if (user == null) {
                throw new InvalidCredentialsException("Invalid username or password");
            }

            String token = jwtService.generateToken(user);
            return new Metadata("Bearer " + token, user);
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }


    public UserModel findById(Long id){
        return userRepository.findById(id).orElseThrow();
    }

    //READ
    public UserModel findByUsername(String username) throws Exception {
        System.out.println("TEST LOGIN SENDING USERNAME ::: "+username);
        UserModel user = userRepository.findByUsername(username);
        System.out.println("TEST LOGIN RETURN USERNAME ::: "+user.getUsername());

        System.out.println("TEST LOGIN RETURN USER ::: "+user);
        return user;
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
