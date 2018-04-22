package com.school.teachermanage.repository;

import com.school.teachermanage.entity.ScoreRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 积分记录
 *
 * @author zhangsl
 * @date 2017-11-05
 */
@Repository
public interface ScoreRecordRepository extends PagingAndSortingRepository<ScoreRecord, Long> {

    /**
     * 根据用户Id查询积分记录
     *
     * @param userId
     * @return 分页的记录
     */
    @Query("select scoreRecord from ScoreRecord scoreRecord where scoreRecord.user.id = :userId")
    Page<ScoreRecord> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 积分记录分页查询
     *
     * @param start
     * @param end
     * @param pageable
     * @return
     */
    @Query("select scoreRecord from ScoreRecord scoreRecord where  scoreRecord.createDate >= :start and scoreRecord.createDate <= :end ")
    Page<ScoreRecord> findScoreRecordsByDate(@Param("start") Date start, @Param("end") Date end, Pageable pageable);

    /**
     * 查询 isFrozened  记录
     * @param userId
     * @param pageable
     * @return
     */
    @Query("select scoreRecord from ScoreRecord scoreRecord where scoreRecord.user.id = :userId and scoreRecord.frozened = :frozened ")
    Page<ScoreRecord> findScoreRecordByUserIdAndType(@Param("userId") Long userId, @Param("frozened") boolean frozened, Pageable pageable);
}
