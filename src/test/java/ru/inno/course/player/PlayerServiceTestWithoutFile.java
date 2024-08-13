package ru.inno.course.player;

import org.junit.jupiter.api.*;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTestWithoutFile {

    PlayerService service;
    String NICKNAME1 = "Nikita";
    String NICKNAME2 = "Mariya";
    String NICKNAME3 = "Sonya";
    int pointsToAdd = 45;

    @BeforeEach
    public void SetUp() {
        service = new PlayerServiceImpl();
    }

    @AfterEach
    public void TearDown() throws IOException {
        Files.deleteIfExists(Path.of("./data.json"));
    }

    @Test
    @DisplayName("Добавление нового игрока. Предусловие: список удален")
    @Tags({@Tag("позитивный"), @Tag("CRITICAL")})
    public void iCanAddPlayer() {
        int playerId = service.createPlayer(NICKNAME1);

        assertEquals(1, playerId);
    }

    @Test
    @DisplayName("Получение игрока по ID. Предусловие: список удален")
    @Tags({@Tag("позитивный"), @Tag("CRITICAL")})
    public void iCanGetPlayerById() {
        int playerId = service.createPlayer(NICKNAME1);
        Player player = service.getPlayerById(playerId);

        assertEquals(1, player.getId());
        assertEquals(NICKNAME1, player.getNick());
        assertEquals(0, player.getPoints());
        assertTrue(player.isOnline());
    }

    @Test
    @DisplayName("Добавление новому игроку очков. Предусловие: список удален")
    @Tag("позитивные")
    public void iCanAddPoints() {
        int playerId = service.createPlayer(NICKNAME1);
        Player player = service.getPlayerById(playerId);
        int playerPoints = service.addPoints(playerId, pointsToAdd);

        assertEquals(pointsToAdd, playerPoints);
    }

    @Test
    @DisplayName("Получение коллекции игроков. Предусловие: список удален")
    @Tag("позитивные")
    public void iCanGetPlayers() {
        int player1Id = service.createPlayer(NICKNAME1);
        int player2Id = service.createPlayer(NICKNAME2);
        Collection<Player> players = service.getPlayers();
        // Переводим коллекцию в массив, чтобы обратиться к первым двум элементам
        //Player[] playersArray = (Player[]) players.toArray();
        Player[] playersArray = players.toArray(new Player[0]);

        assertEquals(2, (long) players.size());
        assertEquals(NICKNAME1, playersArray[0].getNick());
        assertEquals(NICKNAME2, playersArray[1].getNick());

    }

    @Test
    @DisplayName("Удаление игрока. Предусловие: список удален")
    @Tag("позитивные")
    public void iCanDeletePlayer() {
        int playerId = service.createPlayer(NICKNAME1);
        assertEquals(1, service.getPlayers().size());

        service.deletePlayer(playerId);
        assertEquals(0, service.getPlayers().size());
    }

    @Test
    @DisplayName("Повторное добавление игроку очков, добавляем менее 100. Предусловие: список удален")
    @Tag("позитивные")
    public void iCanAddPointsAgain() {
        int playerId = service.createPlayer(NICKNAME1);
        Player player = service.getPlayerById(playerId);
        int playerPoints = service.addPoints(playerId, pointsToAdd);
        assertEquals(pointsToAdd, playerPoints);

        int playerPointsFinal = service.addPoints(playerId, pointsToAdd);
        assertEquals(pointsToAdd + pointsToAdd, playerPointsFinal);
    }

    @Test
    @DisplayName("ID игрока остается уникальным после удаления и добавления игрока. Предусловие: список удален")
    @Tag("позитивные")
    public void playerIdIsUnique() {
        service.createPlayer(NICKNAME1);
        service.createPlayer(NICKNAME2);
        service.deletePlayer(1);
        int playerById = service.createPlayer(NICKNAME3);

        assertEquals(3, playerById);
    }

    @Test
    @DisplayName("Не могу добавить игрока с уже существущим именем. Предусловие: список удален")
    @Tag("негативные")
    //Получается в этом случае нужно было заглянуть в реализацию метода, чтобы узнать, какое исключение он выбрасывает?
    public void iCanNotAddDuplicatedPlayer() {
        service.createPlayer(NICKNAME1);

        assertThrows(IllegalArgumentException.class, () -> service.createPlayer(NICKNAME1));

    }

}
