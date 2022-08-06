package com.memolyze.utility;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.memolyze.entity.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UpdateUserContext {
  public void updateSecurityContext(User loginUser) {
    UserDetails user = new User(loginUser.getId(), loginUser.getName(), loginUser.getUsername(), loginUser.getPassword());

    SecurityContext context = SecurityContextHolder.getContext();

    context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
  }
}
