package app;

import domain.*;
import presentation.Presentation;

public class john {
    public static void main(String[] args) throws Exception {
        Presentation presentation = new Presentation();
        GameLoop gameLoop = new GameLoop(presentation);
        gameLoop.run();
    }
}
