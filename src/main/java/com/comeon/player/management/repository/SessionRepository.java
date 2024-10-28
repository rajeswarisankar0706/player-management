package com.comeon.player.management.repository;

import com.comeon.player.management.model.Player;
import com.comeon.player.management.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findBySessionId(String SessionId);

    List<Session> findByPlayerId(Long playerId);

}
