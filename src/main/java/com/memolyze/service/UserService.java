package com.memolyze.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.memolyze.entity.TmpUser;
import com.memolyze.entity.User;
import com.memolyze.repository.TmpUserRepository;
import com.memolyze.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
  
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private TmpUserRepository tmpUserRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public User loadUserByUsername(String username) throws UsernameNotFoundException {
    if (username == null || username.length() == 0) {
      throw new UsernameNotFoundException("");
    }

    User user = userRepository.findUserByUsername(username);

    if(user == null) {
      throw new UsernameNotFoundException("");
    }

    return user;
  }

  // User系
  public boolean userExists(String username) {
   boolean userExists =  userRepository.existsByUsername(username);
  
   return userExists;
  }
  
  public void saveRegisteredUser(String uuid) {
    TmpUser tmpUser = tmpUserRepository.findByUuid(uuid);
    User registeredUser = new User(null, tmpUser.getName(), tmpUser.getUsername(), tmpUser.getPassword());
    userRepository.save(registeredUser);
    tmpUserRepository.deleteByUUID(uuid);
  }

  public void accountDelete(int userId) {
    userRepository.deleteById(userId);
  }

  public void updateName(String name, int userId) {
    userRepository.updateName(name, userId);
  }

  public void updateMail(String uuid) {
    TmpUser tmpUser = tmpUserRepository.findByUuid(uuid);
    String mail = tmpUser.getUsername();
    int userId = tmpUser.getUserId();
    userRepository.updateMail(mail, userId);
    tmpUserRepository.deleteByUUID(uuid);
  }

  public void updatePassword(String password, int userId) {
    userRepository.updatePass(password, userId);
  }

  // TmpUser系
  // ユーザー登録用
  public void saveTmpUser(String name, String mail, String password, String uuid) {
    TmpUser tmpUser = new TmpUser(name, mail, passwordEncoder.encode(password), uuid);
    if (tmpUserRepository.existsByUsername(mail)) {
      tmpUserRepository.deleteByUsername(mail);
    }
    tmpUserRepository.save(tmpUser);
  }

  // メールアドレス変更用
  public void saveTmpUser(String name, String mail, String password, String uuid, int userId) {
    TmpUser tmpUser = new TmpUser(name, mail, passwordEncoder.encode(password), uuid, userId);
    tmpUserRepository.save(tmpUser);
  }

  public boolean tmpUserExists(String uuid) {
    boolean tmpUserExists =  tmpUserRepository.existsByUUID(uuid);
   
    return tmpUserExists;
  }

  public boolean tmpUserExistsByUsername(String username) {
    boolean tmpUserExists =  tmpUserRepository.existsByUsername(username);
   
    return tmpUserExists;
  }

  public boolean tmpUserExistsInUsers(String uuid) {
    TmpUser tmpUser = tmpUserRepository.findByUuid(uuid);
    String username = tmpUser.getUsername();
    boolean tmpUserExistsInUsers =  this.userExists(username);
   
    return tmpUserExistsInUsers;
   }

  public TmpUser findTmpUserByUsername(String mail) {
  TmpUser tmpUser = tmpUserRepository.findByUsername(mail);
  return tmpUser;
  }

  public TmpUser findTmpUserByUuid(String uuid) {
    TmpUser tmpUser = tmpUserRepository.findByUuid(uuid);
    return tmpUser;
  }
  
  public boolean duplicateTmpUserCheck(int userId) {
    boolean duplicate = tmpUserRepository.existsByUserId(userId);
    return duplicate;
  }

  public void duplicateTmpUserDelete(int userId) {
    tmpUserRepository.deleteByUserId(userId);
  }
}
