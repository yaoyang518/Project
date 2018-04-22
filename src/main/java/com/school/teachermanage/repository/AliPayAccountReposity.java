package com.school.teachermanage.repository;

import com.school.teachermanage.entity.AliPayAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wudc
 * @Date 2017/11/21.
 */
@Repository
public interface AliPayAccountReposity extends PagingAndSortingRepository<AliPayAccount, Long> {

    @Query("select aliPayAccount from  AliPayAccount aliPayAccount where aliPayAccount.userId =:userId " +
            "and aliPayAccount.accountNumber =:accountNumber and aliPayAccount.userName=:userName")
    AliPayAccount findByNameAndAccountAndUserId(@Param("accountNumber") String accountNumber,
                                                @Param("userName") String userName,
                                                @Param("userId") Long userId);

    @Query("select aliPayAccount from  AliPayAccount aliPayAccount where aliPayAccount.userId =:userId and aliPayAccount.asDefault =true")
    AliPayAccount findDefaultAliPayByUserId(@Param("userId") Long userId);

    @Query("select aliPayAccount from AliPayAccount aliPayAccount  where aliPayAccount.userId =:userId ")
    List<AliPayAccount> findByUserId(@Param("userId") Long userId);

    @Query("select aliPayAccount from AliPayAccount aliPayAccount  where aliPayAccount.userId =:userId and aliPayAccount.id =:id ")
    AliPayAccount findByUserIdAndId(@Param("userId") Long userId,
                                    @Param("id") Long id);

    @Query("select count(aliPayAccount) from  AliPayAccount aliPayAccount where aliPayAccount.userId =:userId ")
    int getAliPayAccountSizeByUserId(@Param("userId") Long userId);

}
