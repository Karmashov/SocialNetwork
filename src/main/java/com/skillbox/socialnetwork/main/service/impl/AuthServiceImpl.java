package com.skillbox.socialnetwork.main.service.impl;

import com.skillbox.socialnetwork.main.dto.auth.request.AuthenticationRequestDto;
import com.skillbox.socialnetwork.main.dto.auth.request.RegisterRequestDto;
import com.skillbox.socialnetwork.main.dto.auth.response.AuthResponseFactory;
import com.skillbox.socialnetwork.main.dto.profile.PasswordSetRequestDto;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponseDto;
import com.skillbox.socialnetwork.main.dto.universal.ErrorResponseDto;
import com.skillbox.socialnetwork.main.dto.universal.MessageResponseDto;
import com.skillbox.socialnetwork.main.dto.universal.ResponseDto;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.security.jwt.JwtAuthenticationException;
import com.skillbox.socialnetwork.main.security.jwt.JwtTokenProvider;
import com.skillbox.socialnetwork.main.service.AuthService;
import com.skillbox.socialnetwork.main.service.EmailService;
import com.skillbox.socialnetwork.main.service.PersonService;
import com.skillbox.socialnetwork.main.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PersonService personService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;


    @Value("${project.name}")
    private String projectName;


    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, PersonService personService, EmailService emailService, BCryptPasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.personService = personService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseDto login(AuthenticationRequestDto request) {
        try {
            String email = request.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));
            Person user = personService.findByEmail(email);

            if (user == null) {
                throw new UsernameNotFoundException("User with username: " + email + " not found");
            }

            String token = jwtTokenProvider.createToken(email);
            return AuthResponseFactory.getAuthResponse(user, token);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public ResponseDto register(RegisterRequestDto request) {

        ResponseDto registration = personService.registration(request);
        if (!(registration instanceof ErrorResponseDto))
        emailService.sendSimpleMessageUsingTemplate(request.getEmail(), projectName, request.getFirstName(), "Рады приветствовать Вас на нашем ресурсе!");
        return registration;
    }

    @Override
    public void logout(String token) {
        String email = jwtTokenProvider.getUsername(token);
        personService.logout(personService.findByEmail(email));
    }

    @Override
    public Person getAuthorizedUser(String token) throws JwtAuthenticationException {
        return personService.findByEmail(jwtTokenProvider.getUsername(token));
    }

    @Override
    public boolean isAuthorized(String token) {
        try {
            jwtTokenProvider.getUsername(token);
            return true;
        } catch (JwtAuthenticationException e){
            return false;
        }
    }

    @Override
    public ResponseDto passwordRecovery(String email, String url) {
        Person person = personService.findByEmail(email);
        if (person != null) {
            person.setConfirmationCode(CodeGenerator.codeGenerator());
            personService.save(person);
            String token = jwtTokenProvider.createToken(person.getEmail() + ":" + person.getConfirmationCode());
            emailService.sendPasswordRecovery(email, projectName, person.getFirstName(), url + "/change-password?token=" + token);
            return new BaseResponseDto(new MessageResponseDto("ok"));
        } else {
            return new ErrorResponseDto("invalid_request", "Данный email не найден");
        }
    }

    @Override
    public ResponseDto passwordSet(PasswordSetRequestDto dto, String referer) {
        ResponseDto responseDto;
        try {
            URL ub = new URL(referer);
            dto.setToken(ub.getQuery());
            String token = dto.getToken().replaceAll("token=", "");
            String[] strings = jwtTokenProvider.getUsername(token).split(":");
            if(strings.length == 2){
                Person person = personService.findByEmail(strings[0]);
                if(person.getConfirmationCode().equals(strings[1])){
                    person.setPassword(passwordEncoder.encode(dto.getPassword()));
                    person.setConfirmationCode("");
                    personService.save(person);
                    return new BaseResponseDto(new MessageResponseDto("ok"));
                }
            }
            return new ErrorResponseDto("invalid_request", "token error");

        } catch (MalformedURLException e) {
            responseDto = new ErrorResponseDto("invalid_request", "token not found");
        }
        return responseDto;
    }



}
