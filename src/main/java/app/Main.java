package app;

import domain.GameLoop;
import domain.player.*;
import presentation.Presentation;

public class Main {
    public static void main(String[] args) throws Exception {
        Presentation presentation = new Presentation();
        GameLoop gameLoop = new GameLoop(presentation);
        gameLoop.run();
    }
}
