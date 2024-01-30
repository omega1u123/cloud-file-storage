package com.example.filemanager.service;

import com.example.filemanager.domain.MyUserDetails;
import com.example.filemanager.domain.UserDao;
import com.example.filemanager.domain.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userDao.findByUsername(username);
        if (userEntity != null) return new MyUserDetails(userEntity);
        throw new UsernameNotFoundException("User not found");
    }
}
