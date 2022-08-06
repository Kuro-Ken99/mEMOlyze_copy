package com.memolyze.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.memolyze.entity.Memo;
import com.memolyze.repository.MemoRepository;

@Service
public class MemoCRUDService {
  
  @Autowired
  MemoRepository memoRepository;

  public List<Memo> findMemo(int userId, String memoType) {
    List<Memo> memosList = memoRepository.findByUserIdAndMemoTypeOrderByCreatedAtDesc(userId, memoType);
    return memosList;
  }
  
  public List<String> findMonthList(int userId) {
    List<String> monthList =  memoRepository.findMonth(userId);
    return monthList;
  }

  public Memo findMemo(int memoId) {
    Memo memo = memoRepository.findById(memoId).orElse(null);
    return memo;
  }

  public List<Memo> findArchiveMemo(int userId, LocalDateTime fromDate, LocalDateTime toDate) {
    List<Memo> archiveList = memoRepository.findArchive(userId, fromDate, toDate);
    return archiveList;
  }

  public void createMemo(int userId, String title, String note, String memoType) {
    Memo memo = new Memo(userId, title, note, memoType);
    memoRepository.save(memo);
  }
    
  public void updateMemo(String title, String memo, String memoType, int memoId, int userId) {
    memoRepository.update(title, memo, memoType, memoId, userId);
  }


  public void deleteMemo(int memoId, int  userId) {
    memoRepository.deleteByIdAndUserId(memoId, userId);
  }

  public List<Memo> searchMemo(int userId, String freeword, String memoType, LocalDateTime fromdate, LocalDateTime todate) {
    List<Memo> searchedList =  memoRepository.search(userId, freeword, memoType, fromdate, todate);
    return searchedList;
  }

  public List<Memo> searchMemoFromAll(int userId, String freeword, LocalDateTime fromdate, LocalDateTime todate) {
    List<Memo> searchedList =  memoRepository.findByUserIdAndMemoLikeAndCreatedAtBetweenOrderByCreatedAtDesc(userId, freeword, fromdate, todate);
    return searchedList;
  }
}