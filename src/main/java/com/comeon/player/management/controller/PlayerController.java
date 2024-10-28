package com.comeon.player.management.controller;

import com.comeon.player.management.model.Player;
import com.comeon.player.management.model.Response;
import com.comeon.player.management.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Ok");
    }

    @PostMapping("/register")
    public ResponseEntity<Response> registerPlayer(@RequestBody Player player) {
        Player registerPlayer = playerService.registerPlayer(player);
        if(registerPlayer != null) {
            return ResponseEntity.status(200).body(new Response("Player registered Successfully !!!"));
        }
        return ResponseEntity.status(403).body(new Response("Failed to create a Player at the moment"));
    }

    @PostMapping("/login")
    public ResponseEntity<Response> loginPlayer(@RequestBody Map<String, String> loginData) {
        Optional<Player> playerOpt = playerService.loginPlayer(loginData.get("email"), loginData.get("password"));
        if(playerOpt.isPresent()) {
            return ResponseEntity.status(200).body(new Response("You have successfully logged in!"));
        }
        return ResponseEntity.status(401).body(new Response("Player not found or time limit has reached"));
    }

    @PostMapping("/logout/sessionId")
    public ResponseEntity<Response> logoutPlayer(@RequestParam String sessionId) {
        playerService.logoutPlayer(sessionId);
        return ResponseEntity.status(200).body(new Response("You have successfully logged out. See you next time!"));
    }

    @PostMapping("/players/{playerId}/setTimeLimit/timeLimitInSeconds")
    public ResponseEntity<Response> setPlayerTimeLimit(@PathVariable Long playerId, @RequestParam Long timeLimitInSeconds) {
        Response response = playerService.setPlayerTimeLimit(playerId, timeLimitInSeconds);
        return ResponseEntity.status(200).body(response);
    }
}
