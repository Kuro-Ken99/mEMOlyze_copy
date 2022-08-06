package com.memolyze.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.memolyze.entity.Memo;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Integer> {

  List<Memo> findByUserIdAndMemoTypeOrderByCreatedAtDesc(int userId, String memoType);

  @Query(value = "SELECT DISTINCT DATE_TRUNC('MONTH', created_at) AS MONTH FROM memos WHERE user_id = ?1 AND memo_type = 'memo' ORDER BY MONTH DESC", nativeQuery = true)
  public List<String> findMonth(int userId);

  @Modifying
  @Transactional
  @Query(value = "UPDATE memos SET title = ?1, memo = ?2, memo_type = ?3 WHERE id = ?4 AND user_id = ?5", nativeQuery = true)
  public void update(String title, String memo, String memoType, int memoId, int userId);

  @Transactional
  void deleteByIdAndUserId(int memoId, int userId);

  // メモ・下書き、いずれかが検索対象の場合
  @Query(value = "SELECT * FROM memos WHERE user_id = ?1 AND (memo LIKE ?2 OR title LIKE ?2) AND memo_type = ?3 AND created_at BETWEEN ?4 AND ?5 ORDER BY created_at DESC", nativeQuery = true)
  public List<Memo> search(int userId, String freeword, String memoType, LocalDateTime fromdate, LocalDateTime todate);

  // メモ・下書き、いずれも検索対象の場合
  List<Memo> findByUserIdAndMemoLikeAndCreatedAtBetweenOrderByCreatedAtDesc(int userId, String freeword, LocalDateTime fromdate, LocalDateTime todate);

  @Query(value = "SELECT * FROM memos WHERE user_id = ?1 AND memo_type = 'memo' AND created_at BETWEEN ?2 AND ?3 ORDER BY created_at DESC", nativeQuery = true)
  public List<Memo> findArchive(int userId, LocalDateTime fromDate, LocalDateTime toDate);

  // 形態素解析用にmemoの文字列のみを抽出
  @Query(value = "SELECT memo FROM memos WHERE user_id = ?1 AND memo_type = 'memo' AND created_at BETWEEN ?2 AND ?3 ORDER BY created_at DESC", nativeQuery = true)
  public List<String> findText(int userId, LocalDateTime fromDate, LocalDateTime toDate);
}
