package com.comeon.player.management;

import com.comeon.player.management.model.Player;
import com.comeon.player.management.model.Response;
import com.comeon.player.management.model.Session;
import com.comeon.player.management.repository.PlayerRepository;
import com.comeon.player.management.repository.SessionRepository;
import com.comeon.player.management.service.PlayerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@ContextConfiguration(classes = PlayerManagementApplication.class)
@AutoConfigureMockMvc
public class PlayerControllerTest {

	@Mock
	private PlayerRepository playerRepository; // Mock the PlayerRepository

	@Mock
	private SessionRepository sessionRepository; // Mock the SessionRepository

	@Mock
	private BCryptPasswordEncoder passwordEncoder; // Mock the BCryptPasswordEncoder

	@InjectMocks
	private PlayerService playerService; // Inject the mocks into the PlayerService

	private Player player;

	@BeforeEach
	public void setUp() {
		player = new Player();
		player.setId(1L);
		player.setAddress("solna");
		player.setEmail("test@example.com");
		player.setPassword("password");
		player.setName("Test Player");
		player.setSurname("Test ");
		player.setDateOfBirth(LocalDate.now());
		player.setDailyTimeLimitInSeconds(60L);
	}

	@Test
	@WithMockUser // Mock an authenticated user for the test
	public void testRegisterPlayer_Success() throws Exception {
		when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
		when(playerRepository.save(any(Player.class))).thenReturn(player);
		Player result = playerService.registerPlayer(player);
		Assertions.assertNotNull(result);
		assertEquals("encodedPassword", result.getPassword());
		verify(playerRepository, times(1)).save(any(Player.class));


	}

	@Test
	public void testLoginPlayer_Success() {
		when(playerRepository.findByEmail(player.getEmail())).thenReturn(Optional.of(player));
		when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);
		when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Optional<Player> result = playerService.loginPlayer(player.getEmail(), player.getPassword());

		Assertions.assertTrue(result.isPresent());
		assertEquals(player, result.get());
		verify(sessionRepository, times(1)).save(any(Session.class));
	}

	@Test
	public void testLoginPlayer_Failure() {
		when(playerRepository.findByEmail(player.getEmail())).thenReturn(Optional.empty());

		Optional<Player> result = playerService.loginPlayer(player.getEmail(), player.getPassword());

		Assertions.assertFalse(result.isPresent());
		verify(sessionRepository, never()).save(any(Session.class));
	}

	@Test
	public void testSetPlayerTimeLimit_Success() {
		when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));
		when(playerRepository.save(any(Player.class))).thenReturn(player);

		Response response = playerService.setPlayerTimeLimit(player.getId(), 7200L);

		assertEquals("Great! The player's time limit has been set successfully!", response.getMessage());
		assertEquals(7200L, player.getDailyTimeLimitInSeconds());
		verify(playerRepository, times(1)).save(any(Player.class));
	}

	@Test
	public void testSetPlayerTimeLimit_Failure() {
		when(playerRepository.findById(player.getId())).thenReturn(Optional.empty());

		Response response = playerService.setPlayerTimeLimit(player.getId(), 7200L);

		assertEquals("Its not be possible to set limit for an inactive player!!!", response.getMessage());
		verify(playerRepository, never()).save(any(Player.class));
	}

}

