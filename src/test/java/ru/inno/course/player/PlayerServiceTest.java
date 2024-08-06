package ru.inno.course.player;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.inno.course.player.ext.BeforeEachDemo;
import ru.inno.course.player.ext.MyTestWatcher;
import ru.inno.course.player.ext.PlayersAndPointsProvider;
import ru.inno.course.player.ext.PointsProvider;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.NoSuchElementException;


import static org.junit.jupiter.api.Assertions.*;

// 1. Тест не должен настраивать свое окружение.
// 2. Главный код ничего не знает про тесты.
// 3. В тестах не должно быть if'ов

@ExtendWith(MyTestWatcher.class)
//@ExtendWith(ParameterResolver.class)
//@ExtendWith(BeforeEachDemo.class)
public class PlayerServiceTest {
    private PlayerService service;
    private static final String NICKNAME = "Nikita";

    // hooks - хуки
    @BeforeEach
    public void setUp() {
        service = new PlayerServiceImpl();
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("./data.json"));
    }

    @Test
    @Tags({@Tag("позитивный"), @Tag("CRITICAL")})
    //@Disabled("Jira-123")
    @DisplayName("Создаем игрока и проверяем его значения по дефолту")
    public void iCanAddNewPlayer() {
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size());

        int nikitaId = service.createPlayer(NICKNAME);
        Player playerById = service.getPlayerById(nikitaId);

        assertEquals(nikitaId, playerById.getId());
        assertEquals(0, playerById.getPoints());
        assertEquals(NICKNAME, playerById.getNick());
        assertTrue(playerById.isOnline());
    }

    @Test
    @DisplayName("3. (нет json-файла) добавить игрока")
    public void iCanAddPlayer() {
        int playerId = service.createPlayer(NICKNAME);
        assertEquals(1, playerId);
    }

    @Test
    @DisplayName("7. (добавить игрока) - получить игрока по id (3, 5)")
    public void iCanAddPlayerIntoFile() {
        int playerId = service.createPlayer(NICKNAME);
        Player playerById = service.getPlayerById(playerId);
        assertEquals(1, playerById.getId());
        assertEquals(NICKNAME, playerById.getNick());
        assertEquals(0, playerById.getPoints());
        assertTrue(playerById.isOnline());
    }

    @Test
    @Tag("позитивные")
    @DisplayName("(добавить игрока) - удалить игрока - проверить отсутствие в списке (2,3)")
    public void iCanDeletePlayer() {
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size());

        int newPlayerId = service.createPlayer(NICKNAME);
        Collection<Player> listAfterAdd = service.getPlayers();
        assertEquals(1, listAfterAdd.size());

        service.deletePlayer(newPlayerId);
        Collection<Player> listAfterDelete = service.getPlayers();
        assertEquals(0, listAfterDelete.size());
//        Player playerById = service.getPlayerById(playerId);
//        assertEquals(playerId, playerById.getId());

        //service.deletePlayer(playerById.getId());
    }

    @Test
    @Tag("негативный")
    @DisplayName("Нельзя создать дубликат игрока")
    public void iCannotCreateADuplicate() {
        service.createPlayer(NICKNAME);
        assertThrows(IllegalArgumentException.class, () -> service.createPlayer(NICKNAME));
    }

    @Test
    @Tag("позитивные")
    @DisplayName("Нельзя получить несуществующего пользователя")
    public void iCannotGetEmptyUser() {
        assertThrows(IllegalArgumentException.class, () -> service.getPlayerById(9999));
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, -50, 0, 100, -5000000})
    @DisplayName("Добавление очков игроку")
    public void iCanAddPoints(int points) {
        int playerId = service.createPlayer(NICKNAME);
        service.addPoints(playerId, points);
        Player playerById = service.getPlayerById(playerId);
        assertEquals(points, playerById.getPoints());
    }

    @ParameterizedTest
    @ArgumentsSource(PointsProvider.class)
    @DisplayName("Добавление очков игроку")
    public void iCanAddPoints2(int pointsToAdd, int pointsToBe) {
        int playerId = service.createPlayer(NICKNAME);
        service.addPoints(playerId, pointsToAdd);
        Player playerById = service.getPlayerById(playerId);
        assertEquals(pointsToBe, playerById.getPoints());
    }

    @ParameterizedTest
    @ArgumentsSource(PlayersAndPointsProvider.class)
    @DisplayName("Добавление очков игроку c ненулевым балансом")
    public void iCanAddPoints3(Player player, int pointsToAdd, int pointsToBe) {
        int id = service.createPlayer(player.getNick());
        service.addPoints(id, player.getPoints());

        service.addPoints(id, pointsToAdd);
        Player playerById = service.getPlayerById(id);
        assertEquals(pointsToBe, playerById.getPoints());
    }


}
