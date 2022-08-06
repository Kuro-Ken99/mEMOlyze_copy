package com.memolyze.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.memolyze.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  User findUserByUsername(String username);

  boolean existsByUsername(String mail);

  User deleteById(int userId);

  @Modifying
  @Transactional
  @Query(value = "UPDATE users SET name = ?1 WHERE id = ?2", nativeQuery = true)
  public void updateName(String name, int userId);

  @Modifying
  @Transactional
  @Query(value = "UPDATE users SET mail = ?1 WHERE id = ?2", nativeQuery = true)
  public void updateMail(String mail, int userId);

  // mypageからのパスワード変更処理時
  @Modifying
  @Transactional
  @Query(value = "UPDATE users SET password = ?1 WHERE id = ?2", nativeQuery = true)
  public void updatePass(String password, int userId);

  // パスワード紛失時
  @Modifying
  @Transactional
  @Query(value = "UPDATE users SET password = ?1 WHERE mail = ?2", nativeQuery = true)
  public void updatePass(String password, String mail);


}
