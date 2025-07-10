package com.rootconnect.rootconnect.repository;


import com.rootconnect.rootconnect.model.User;
import com.rootconnect.rootconnect.model.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {
    List<UserInteraction> findBySourceUser(User user);

    boolean existsBySourceUserAndTargetUser(User source, User target);
}
