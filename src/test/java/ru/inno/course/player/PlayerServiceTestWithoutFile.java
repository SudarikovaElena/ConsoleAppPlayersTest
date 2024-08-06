package ru.inno.course.player;

import org.junit.jupiter.api.*;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTestWithoutFile {

    PlayerService service;
    String NICKNAME1 = "Nikita";
    String NICKNAME2 = "Mariya";
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
    @DisplayName("Списка нет - добавление нового игрока")
    public void iCanAddPlayer() {
        int playerId = service.createPlayer(NICKNAME1);
        assertEquals(1,playerId);
    }

    @Test
    @DisplayName("Списка нет - получение игрока по ID")
    public void iCanGetPlayerById() {
        int playerId = service.createPlayer(NICKNAME1);
        Player player = service.getPlayerById(playerId);
        assertEquals(1, player.getId());
        assertEquals(NICKNAME1, player.getNick());
        assertEquals(0, player.getPoints());
        assertTrue(player.isOnline());
    }

    @Test
    @DisplayName("Списка нет - добавление игроку очков") // Нужно делать, когда список есть? Когда можем взять одного из игроков
    // узнать сколько у него очков и добавить? Нет, это может быть уже повторное добавление очков, нам нужно убдитьсся что игроку
    //еще не добавлялись очки, т.е. нужно таки создать его с нуля в этом же тесте
    @Tag("позитивные")
    public void iCanAddPoints() {
        int playerId = service.createPlayer(NICKNAME1);
        Player player = service.getPlayerById(playerId);
        int playerPoints = service.addPoints(playerId,pointsToAdd);
        assertEquals(pointsToAdd, playerPoints);
    }

    @Test
    @DisplayName("Списка нет - получение коллекции игроков")
    @Tag("позитивные")
    public void iCanGetPlayers() {
        int player1Id = service.createPlayer(NICKNAME1);
        int player2Id = service.createPlayer(NICKNAME2);
//        Collection<Player> players = (List<Player>) service.getPlayers(); //Возникает ошибка в проверках ниже - непонятно,
//        как работать с Collection<Player> - как достать 1го игрока? 2го?
//        assertEquals(NICKNAME1, players.get(0).getNick());
//        assertEquals(NICKNAME2, players.get(1).getNick());
        Collection<Player> players = service.getPlayers();
        assertEquals(2, (long) players.size());

    }

    @Test
    @DisplayName("Списка нет - удаление игрока")
    @Tag("позитивные")
    public void iCanDeletePlayer() {
        int playerId = service.createPlayer(NICKNAME1);
        assertEquals(1, service.getPlayers().size());
        service.deletePlayer(playerId);
        assertEquals(0, service.getPlayers().size());
    }

    @Test
    @DisplayName("Списка нет - повторное добавление игроку очков")
    @Tag("позитивные")
    public void iCanAddPointsAgain() {
        int playerId = service.createPlayer(NICKNAME1);
        Player player = service.getPlayerById(playerId);
        int playerPoints = service.addPoints(playerId,pointsToAdd);
        assertEquals(pointsToAdd, playerPoints);

        int playerPointsFinal = service.addPoints(playerId,pointsToAdd);
        assertEquals(pointsToAdd + pointsToAdd, playerPointsFinal);
    }

    @Test
    @DisplayName("Не могу добавить игрока с уже существущим именем")
    @Tag("негативные")
    public void iCanNotAddDuplicate() {
        service.createPlayer(NICKNAME1);
        assertThrows(IllegalArgumentException.class, () -> service.createPlayer(NICKNAME1));

    }

}
