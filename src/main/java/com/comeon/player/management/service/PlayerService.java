package com.comeon.player.management.service;

import com.comeon.player.management.model.Player;
import com.comeon.player.management.model.Response;
import com.comeon.player.management.model.Session;
import com.comeon.player.management.repository.PlayerRepository;
import com.comeon.player.management.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Player registerPlayer(Player player) {
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        return playerRepository.save(player);
    }

    public Optional<Player> loginPlayer(String email, String password) {
        Optional<Player> playerOpt = playerRepository.findByEmail(email);
        if (playerOpt.isPresent() && passwordEncoder.matches(password, playerOpt.get().getPassword())) {
            Player player = playerOpt.get();
            if (isPlayerTimeLimitExceeded(player)) {
                return Optional.empty();
            }

            Session session = new Session();
            session.setPlayer(player);
            session.setLoginTime(LocalDateTime.now());
            session.setSessionId(UUID.randomUUID().toString());
            session.setSessionDate(LocalDate.now()); // Set the current date
            sessionRepository.save(session);
            return Optional.of(player);
        }
            return Optional.empty();
        }

    public void logoutPlayer(String sessionId) {
        Optional<Session> sessionOpt = sessionRepository.findBySessionId(sessionId);
        sessionOpt.ifPresent(session -> {
            if (session.getLoginTime() != null) {
                Duration sessionDuration = Duration.between(session.getLoginTime(), LocalDateTime.now());
                session.addTimeSpent(sessionDuration);
            }
            session.setLogoutTime(LocalDateTime.now());
            sessionRepository.save(session);
        });
    }

    public Response setPlayerTimeLimit(Long playerId, Long timeLimitInSeconds) {
        Optional<Player> playerOpt = playerRepository.findById(playerId);
        if(playerOpt.isPresent()) {
            playerOpt.get().setDailyTimeLimitInSeconds(timeLimitInSeconds);
            playerRepository.save(playerOpt.get());
            return new Response("Great! The player's time limit has been set successfully!");
        }else {
            return new Response("Its not be possible to set limit for an inactive player!!!");
        }
    }

   /* private boolean isPlayerTimeLimitExceeded(Player player) {
        Optional<Session> activeSessionOpt = sessionRepository.findById(player.getId());
        if (activeSessionOpt.isPresent()) {
            Session session = activeSessionOpt.get();
            long timeSpentTodayInSeconds = session.getTimeSpentTodayInSeconds();
            return player.getPassword() != null && timeSpentTodayInSeconds > player.getDailyTimeLimitInSeconds();
        }
        return false;
    }*/

    private boolean isPlayerTimeLimitExceeded(Player player) {
        List<Session> sessions = sessionRepository.findByPlayerId(player.getId());
        long timeSpentTodayInSeconds = 0;
        LocalDate today = LocalDate.now();
        for (Session session : sessions) {
            if (session.getSessionDate().isEqual(today)) {
                if (session.getLogoutTime() == null) {
                    timeSpentTodayInSeconds += Duration.between(session.getLoginTime(), LocalDateTime.now()).getSeconds();
                } else {
                    timeSpentTodayInSeconds += Duration.between(session.getLoginTime(), session.getLogoutTime()).getSeconds();
                }
            }
        }
        return player.getPassword() != null && timeSpentTodayInSeconds > player.getDailyTimeLimitInSeconds();
    }

}
