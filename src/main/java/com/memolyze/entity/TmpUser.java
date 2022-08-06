package com.memolyze.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tmp_users")
@NoArgsConstructor
@Getter
@Setter
public class TmpUser implements UserDetails {

  public TmpUser(String name, String username, String password, String UUID) {
    this.name = name;
    this.username = username;
    this.password = password;
    this.UUID = UUID;
  }

  public TmpUser(String name, String username, String password, String UUID, int userId) {
    this.name = name;
    this.username = username;
    this.password = password;
    this.UUID = UUID;
    this.userId = userId;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  @Column(name="mail")
  private String username;

  private String password;

  @Column(name="uuid")
  private String UUID;

  @Column(name="user_id")
  private Integer userId;

  @OneToOne
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private User user;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isAccountNonExpired() {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public boolean isEnabled() {
    // TODO Auto-generated method stub
    return true;
  }
}
