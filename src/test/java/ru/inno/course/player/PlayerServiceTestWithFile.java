package ru.inno.course.player;

import org.junit.jupiter.api.BeforeEach;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

public class PlayerServiceTestWithFile {

    PlayerService service;

    @BeforeEach
    public void setUp() {
        service = new PlayerServiceImpl();
    }



}
