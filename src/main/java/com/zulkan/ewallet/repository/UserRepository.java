package com.zulkan.ewallet.repository;

import com.zulkan.ewallet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User getUserByToken(String token);

    User getUserByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.balance = u.balance + :amount WHERE u.id = :userId")
    int addBalance(@Param("userId") Integer userId, @Param("amount") Integer amount);
}
