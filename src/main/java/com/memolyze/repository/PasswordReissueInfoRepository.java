package com.memolyze.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.memolyze.entity.Reissue;

@Repository
public interface PasswordReissueInfoRepository extends JpaRepository<Reissue, Integer> {

  Reissue findByToken(String token);

  @Transactional
  void deleteByToken(String token);
  boolean existsByUsername(String username);

  @Transactional
  void deleteByUsername(String username);
}
