package com.memolyze.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserForm {

  @NotBlank(message = "name: 空白のみでは登録できません")
  @Size(max = 30)
  private String name;

  @NotBlank
  private String mail;

  @NotBlank(message = "password: 空白のみでは登録できません")
  @Size(min = 8)
  private String password;
}
