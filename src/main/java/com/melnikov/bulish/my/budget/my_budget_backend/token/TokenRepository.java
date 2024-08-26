package com.melnikov.bulish.my.budget.my_budget_backend.token;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    @Query(value = """
      select t from Token t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
    List<Token> findAllValidTokenByUser(Integer id);

    @Modifying
    @Transactional
    @Query(value = """
            delete from Token where expired = true or revoked = true
            """)
    void cleanAllExpiredToken();
}