package com.memolyze.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.memolyze.entity.TmpUser;

@Repository
public interface TmpUserRepository extends JpaRepository<TmpUser, Integer> {

  boolean existsByUUID(String uuid);

  boolean existsByUsername(String username);

  boolean existsByUserId(int userId);

  @Query(value = "SELECT * FROM tmp_users WHERE uuid = ?1", nativeQuery = true)
  public TmpUser findByUuid(String uuid);

  @Query(value = "SELECT * FROM tmp_users WHERE mail = ?1 ORDER BY id DESC LIMIT 1", nativeQuery = true)
  public TmpUser findByUsername(String username);

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM tmp_users WHERE user_id = ?1", nativeQuery = true)
  public void deleteByUserId(int userId);

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM tmp_users WHERE uuid = ?1", nativeQuery = true)
  public void deleteByUUID(String uuid);

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM tmp_users WHERE mail = ?1", nativeQuery = true)
  public void deleteByUsername(String mail);
}
