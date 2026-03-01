package domain;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import presentation.Presentation;

import java.io.IOException;


public class GameLoop {
    private final Presentation presentation;
    private FSM_State state = FSM_State.START;
    private Game currentGame;
    private boolean running = true;

    public GameLoop(Presentation presentation) {
        this.presentation = presentation;
    }

    private enum FSM_State {
        START,
        PAUSE,
        GAME,
        BACKPACK,
        LEADERS
    }

    public void run() throws IOException {
        presentation.start();

        while (running) {
            render();
            presentation.refresh();

            KeyStroke key = presentation.getScreen().readInput();
            if (key == null) continue;

            processInput(key);
        }

        presentation.end();
    }

    private void render() throws IOException {
        switch (state) {
            case START:
                presentation.displayStartMenu();
                break;
            case PAUSE:
                presentation.displayPauseMenu();
                break;
            case GAME:
                presentation.displayGame(currentGame);
                break;
            case BACKPACK:
                //presentation.displayBackpack(currentGame.getBackpack());
                break;
            case LEADERS:
                presentation.displayLeaderboard();
                break;
        }
    }

    private void processInput(KeyStroke key) throws IOException {
        switch (state) {
            case START:
                handleStartInput(key);
                break;
            case PAUSE:
                handlePauseInput(key);
                break;
            case GAME:
                handleGameInput(key);
                break;
            case BACKPACK:
                handleBackpackInput(key);
                break;
            case LEADERS:
                handleLeadersInput(key);
                break;
            default:
                handleDefaultInput(key);
                break;
        }
    }

    private void handleStartInput(KeyStroke key) throws IOException {
        if (key.getKeyType() == KeyType.Escape) {
            running = false;
            return;
        }

        if (key.getKeyType() == KeyType.Character) {
            char c = key.getCharacter();
            switch (c) {
                case '1':
                    // Ввод имени
                    String name = presentation.displayEnterNameDialog();
                    if (name != null && !name.isEmpty()) {
                        currentGame = new Game(name);
                        currentGame.generateLevel(1);
                        state = FSM_State.GAME;
                    }
                    break;
                case '2':
                    // Загрузка игры
                    loadGame();
                    break;
                case '3':
                    // Таблица лидеров
                    state = FSM_State.LEADERS;
                    break;
            }
        }
    }

    private void handlePauseInput(KeyStroke key) throws IOException {
        if (key.getKeyType() == KeyType.Escape) {
            running = false;
            return;
        }

        if (key.getKeyType() == KeyType.Character) {
            char c = key.getCharacter();
            switch (c) {
                case '1': // Resume
                    state = FSM_State.GAME;
                    break;
                case '2': // New game
                    String name = presentation.displayEnterNameDialog();
                    if (name != null && !name.isEmpty()) {
                        currentGame = null;
                        currentGame = new Game(name);
                        currentGame.generateLevel(1);
                        state = FSM_State.GAME;
                    }
                    break;
                case '3': // Save game
                    saveGame();
                    break;
                case '4': // Load game
                    loadGame();
                    break;
                case '5': // Leaders
                    state = FSM_State.LEADERS;
                    break;
            }
        }
    }

    private void handleGameInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) {
            state = FSM_State.PAUSE;
            return;
        }

        // Обработка символов
        if (key.getKeyType() == KeyType.Character) {
            char c = key.getCharacter();
            switch (c) {
                case 'W':
                case 'w':
                case 'Ц':
                case 'ц':
                    currentGame.moveUp();
                    return;
                case 'S':
                case 's':
                case 'Ы':
                case 'ы':
                    currentGame.moveDown();
                    return;
                case 'A':
                case 'a':
                case 'Ф':
                case 'ф':
                    currentGame.moveLeft();
                    return;
                case 'D':
                case 'd':
                case 'В':
                case 'в':
                    currentGame.moveRight();
                    return;
                case 'p':
                    saveGame();
                    return;
                case 'h':
                    //currentGame.setCurrentBackpack("HEALTH");
                    state = FSM_State.BACKPACK;
                    return;
                case 'j':
                    //currentGame.setCurrentBackpack("WEAPONS");
                    state = FSM_State.BACKPACK;
                    return;
                case 'k':
                    //currentGame.setCurrentBackpack("ITEMS");
                    state = FSM_State.BACKPACK;
                    return;
                case 'e':
                    //currentGame.setCurrentBackpack("EQUIPMENT");
                    state = FSM_State.BACKPACK;
                    return;
            }
        }

    }

    private void handleBackpackInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) {
            state = FSM_State.GAME;
            return;
        }

        if (key.getKeyType() == KeyType.Character) {
            char c = key.getCharacter();
            if (c >= '0' && c <= '9') {
                // Выбор предмета по номеру (0-9)
                int itemIndex = c - '0';
                //currentGame.selectBackpackItem(itemIndex);
            }
        }
    }

    private void handleLeadersInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) {
            // Возвращаемся в предыдущее состояние
            // В данном случае просто в START, но можно сохранять предыдущее состояние
            state = FSM_State.START;
        }
    }

    private void handleDefaultInput(KeyStroke key) {
        // Обработка по умолчанию (можно ничего не делать)
        System.out.println("Необработанная клавиша: " + key);
    }

    // Вспомогательные методы
    private void loadGame() {
        // Логика загрузки игры
        System.out.println("Загрузка игры...");
    }

    private void saveGame() {
        // Логика сохранения
        System.out.println("Игра сохранена");
    }

    private void saveAndExit() {
        saveGame();
        running = false;
    }
}