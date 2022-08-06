package com.memolyze.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CreateMail {

  @Autowired
  MailSender mailSender;
  
  public SimpleMailMessage createMail(String name, String username, String uuid) {
    String title = "mEMOlyze | アカウント確認のお願い";
    String content = name + "さん、この度は仮登録ありがとうございます。" + "\n" + "\n" + "下記リンクより、本登録を完了させてください。" + "\n" + "https://memolyze.herokuapp.com/validate?id=" + uuid;

    SimpleMailMessage msg = new SimpleMailMessage();

    msg.setTo(username);
    msg.setSubject(title);
    msg.setText(content);

    return msg;
  }

  public SimpleMailMessage createContactMail(String name, String mail, String content) {
    String to = "***************";
    String title = "お問合せ";
    String text = name + "さんからお問合せがきました。" + "\n" + content + "sent by" + mail;

    SimpleMailMessage msg = new SimpleMailMessage();

    msg.setTo(to);
    msg.setSubject(title);
    msg.setText(text);

    return msg;
  }

  public SimpleMailMessage createCopiedMail(String name, String mail, String content) {
    String to = mail;
    String title = "mEMOlyze | お問合せありがとうございます";
    String text = name + "さん、この度はお問合せありがとうございました。" + "\n" + "下記内容にて承りました。" + "\n" + "\n" + "お問合せ内容：" + "\n" + content + "\n" + "\n" + "※確認にお時間を頂戴します。少々お待ちくださいませ。";

    SimpleMailMessage msg = new SimpleMailMessage();

    msg.setTo(to);
    msg.setSubject(title);
    msg.setText(text);

    return msg;
  }

  public SimpleMailMessage createUpdateMail(String name, String mail, String uuid) {
    String title = "mEMOlyze | メールアドレス変更";
    String content = name + "さん、メールアドレスの仮変更を受け付けました。" + "\n" + "\n" + "下記リンクより、変更を完了させてください。" + "\n" + "https://memolyze.herokuapp.com/address_update?id=" + uuid;

    SimpleMailMessage msg = new SimpleMailMessage();

    msg.setTo(mail);
    msg.setSubject(title);
    msg.setText(content);

    return msg;
  }

  public SimpleMailMessage registerDoneMail(String name, String mail) {
    String to = mail;
    String title = "mEMOlyze | 登録が完了しました";
    String text = name + "さん、この度はご登録ありがとうございました。" + "\n" + "引き続き、mEMOlyzeをお楽しみくださいませ。" + "\n" + "\n" + "https://memolyze.herokuapp.com/login";

    SimpleMailMessage msg = new SimpleMailMessage();

    msg.setTo(to);
    msg.setSubject(title);
    msg.setText(text);

    return msg;
  }

  public SimpleMailMessage thanksMail(String name, String mail) {
    String to = mail;
    String title = "mEMOlyze | 退会のお知らせ";
    String text = name + "さん、この度はmEMOlyzeをご利用いただき、誠にありがとうございました。" + "\n" + "\n" + "引き続きサービス向上に努めてまいります。" + "\n" +  "またのご利用、心よりお待ち申し上げております。" + "\n" + "\n" + "https://memolyze.herokuapp.com/home";

    SimpleMailMessage msg = new SimpleMailMessage();

    msg.setTo(to);
    msg.setSubject(title);
    msg.setText(text);

    return msg;
  }

  public SimpleMailMessage deleteAnnounce(String name, String opinion) {
    String to = "**************";
    String title = name + "さんが退会しました";
    String text = "ご意見：" + "\n" + opinion;

    SimpleMailMessage msg = new SimpleMailMessage();

    msg.setTo(to);
    msg.setSubject(title);
    msg.setText(text);

    return msg;
  }

  public SimpleMailMessage passReissueMail(String mail, String uuid) {
    String to = mail;
    String title = "mEMOlyze | パスワード再設定";
    String text = "下記リンクより、パスワードの再設定を行なってください。" + "\n" + "\n" + "https://memolyze.herokuapp.com/pass_validate?id=" + uuid;

    SimpleMailMessage msg = new SimpleMailMessage();

    msg.setTo(to);
    msg.setSubject(title);
    msg.setText(text);

    return msg;
  }
}
