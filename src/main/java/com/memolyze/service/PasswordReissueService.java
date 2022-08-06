package com.memolyze.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.memolyze.utility.CreateMail;
import com.memolyze.entity.Reissue;
import com.memolyze.repository.PasswordReissueInfoRepository;
import com.memolyze.repository.UserRepository;

@Service
public class PasswordReissueService {

  @Autowired
  private PasswordGenerator passwordGenerator;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  UserRepository userRepository;
  @Autowired
  PasswordReissueInfoRepository passwordReissueInfoRepository;
  @Autowired
  MailSender mailSender;

  public String createAndSendReissueInfo(String username) {
    List<CharacterRule> rules = Arrays.asList(
      new CharacterRule(EnglishCharacterData.UpperCase, 1),
      new CharacterRule(EnglishCharacterData.LowerCase, 1),
      new CharacterRule(EnglishCharacterData.Digit, 1)
    );
    String rawSecret = passwordGenerator.generatePassword(8, rules);

    // DB内に存在しないメールだった場合、その場で処理を終わらせる
    if(!userRepository.existsByUsername(username)) {
      return rawSecret;
    }
    // 重複分は削除
    if (passwordReissueInfoRepository.existsByUsername(username)) {
      passwordReissueInfoRepository.deleteByUsername(username);
    }

    String token = UUID.randomUUID().toString();
    LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

    Reissue reissue = new Reissue(null, username, token, passwordEncoder.encode(rawSecret), expiryDate);
    passwordReissueInfoRepository.save(reissue);

    SimpleMailMessage msg = CreateMail.passReissueMail(username, token);
    mailSender.send(msg);

    return rawSecret;
  }

  public Reissue findOne(String token) {
    Reissue reissue =  passwordReissueInfoRepository.findByToken(token);

    return reissue;
  }

  public boolean updatePass(String token, String secret, String newPass) {
    Reissue reissue = passwordReissueInfoRepository.findByToken((token));

    // トークンが謝っていればGET通信時にエラー画面に遷移するため、ここに遷移できた時点でトークンに誤りはない。しかし、再発行処理中に再発行メールを再送してしまった場合、reissueはnullになるため、ここでエラー出しをする。
    if(reissue == null || !passwordEncoder.matches(secret, reissue.getSecret()) || LocalDateTime.now().isAfter(reissue.getExpiryDate())) {
      return false;
    } else {
      // パスワード再発行後は不要なのでreissue削除
      passwordReissueInfoRepository.deleteByToken(token);
      userRepository.updatePass(passwordEncoder.encode(newPass), reissue.getUsername());
    }
    return true;
  }
}
