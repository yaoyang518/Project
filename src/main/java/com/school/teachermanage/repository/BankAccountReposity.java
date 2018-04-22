package com.school.teachermanage.repository;

import com.school.teachermanage.entity.BankAccount;
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
public interface BankAccountReposity extends PagingAndSortingRepository<BankAccount, Long> {

    List<BankAccount> findByCardNo(String cardNo);

    @Query("select bankAccount from  BankAccount bankAccount where bankAccount.userId =:userId ")
    List<BankAccount> findCardsByUserId(@Param("userId") Long userId);

    @Query("select bankAccount from  BankAccount bankAccount where bankAccount.userId =:userId and bankAccount.id =:bankAccountId")
    BankAccount findByUserIdAndId(@Param("userId") Long userId, @Param("bankAccountId") Long bankAccountId);

    @Query("select bankAccount from  BankAccount bankAccount where bankAccount.userId =:userId and bankAccount.asDefault =true")
    BankAccount findDefaultBankByUserId(@Param("userId") Long userId);

    @Query("select count(bankAccount) from  BankAccount bankAccount where bankAccount.userId =:userId ")
    int getBankAccountSizeByUserId(@Param("userId") Long userId);

    @Query("select bankAccount from  BankAccount bankAccount where bankAccount.userId =:userId and bankAccount.cardNo =:cardNo")
    BankAccount findByCardNoAndUserId(@Param("cardNo")String cardNo,@Param("userId") Long userId);

}
