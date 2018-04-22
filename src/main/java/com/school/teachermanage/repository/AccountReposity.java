package com.school.teachermanage.repository;

import com.school.teachermanage.entity.Account;
import com.school.teachermanage.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 帐号数据
 *
 * @author zhangsl
 * @date 2017-11-05
 */
@Repository
public interface AccountReposity extends CrudRepository<Account, Long> {

    /**
     * 根据User获取帐户
     *
     * @param user
     * @return {#link Account}
     */
    Account findByUser(User user);

    /**
     * 获取大于 minScore的帐号
     *
     * @param minScore
     * @return List<Account>
     */
    @Query("select  account from Account  account where account.frozenScore >= :minScore ")
    List<Account> findCanConvert(@Param("minScore") BigDecimal minScore);

    /**
     * 获取所有可用积分
     *
     * @return BigDecimal
     */
    @Query("select sum(account.score) from Account  account ")
    BigDecimal getSumScore();

    /**
     * 获取所有冻结积分
     *
     * @return BigDecimal
     */
    @Query("select sum(account.frozenScore) from Account  account ")
    BigDecimal getSumFrozenScore();

    @Query("select account from Account  account where account.user.id=:userId")
    Account findByUserId(@Param("userId") Long userId);
}
