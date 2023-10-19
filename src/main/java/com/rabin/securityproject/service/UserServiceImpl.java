package com.rabin.securityproject.service;

import com.rabin.securityproject.dto.AuthenticateRequest;
import com.rabin.securityproject.dto.AuthenticateResponse;
import com.rabin.securityproject.dto.UserDto;
import com.rabin.securityproject.entity.UserInfo;
import com.rabin.securityproject.repository.UserRepository;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public AuthenticateResponse registrationOfUserInfo(UserDto userDto) throws Exception {
        Optional<UserInfo>existuser=userRepository.findByUsername(userDto.getUsername());
        if(existuser.isPresent()){
            throw new Exception("user already present");
        }else{
            UserInfo userInfo=new UserInfo();
            BeanUtils.copyProperties(userDto,userInfo);
            userInfo.setPassword(passwordEncoder.encode(userDto.getPassword()));
            userRepository.save(userInfo);
            String jwtToken=jwtService.generateToken(userInfo);
//        return AuthenticateResponse.builder()
//                .token(jwtToken)
//                .build();
            return new AuthenticateResponse(jwtToken);
        }
    }

    @Override
    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));

        Optional<UserInfo> existUser=userRepository.findByUsername(request.getUsername());
        if(existUser.isPresent()){
            String jwtToken=jwtService.generateToken(existUser.get());
            return new AuthenticateResponse(jwtToken);
        }else{
            return new AuthenticateResponse("User not found");
        }

    }

    @Override
    public List<UserDto> getAllRecord() {
       return userRepository.findAll().stream()
                .map(userInfo -> {
                    UserDto userDto=new UserDto();
                    BeanUtils.copyProperties(userInfo,userDto);
                    return userDto;
                }).collect(Collectors.toList());

    }

    @Override
    public UserDto getRecordByUsername(String username) {
      Optional<UserInfo>existUser  =userRepository.findByUsername(username);
      if(existUser.isPresent()){
         UserInfo userInfo= existUser.get();
         UserDto userDto=new UserDto();
         BeanUtils.copyProperties(userInfo,userDto);
         return userDto;
      }else{
          throw new UsernameNotFoundException("User not found");
      }

    }
}
