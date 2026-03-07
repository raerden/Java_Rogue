package domain;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import datalayer.LoadSaveData;
import domain.items.ItemType;
import presentation.Presentation;

import java.io.IOException;
import java.lang.Character;

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
            if (currentGame != null && currentGame.getPlayer().getHealth() == 0) {
                state = FSM_State.START;
            }
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
                presentation.displayBackpack(currentGame);
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
            char c = Character.toLowerCase(key.getCharacter());
            switch (c) {
                case 'w': case 'ц':
                    currentGame.moveUp();
                    return;
                case 's': case 'ы':
                    currentGame.moveDown();
                    return;
                case 'a': case 'ф':
                    currentGame.moveLeft();
                    return;
                case 'd': case 'в':
                    currentGame.moveRight();
                    return;
                case 'p':case 'з':
                    saveGame();
                    return;
                case 'h': case 'р':
                    currentGame.setBackpackCurrentItems(ItemType.WEAPON);
                    state = FSM_State.BACKPACK;
                    return;
                case 'j': case 'о':
                    currentGame.setBackpackCurrentItems(ItemType.FOOD);
                    state = FSM_State.BACKPACK;
                    return;
                case 'k': case 'л':
                    currentGame.setBackpackCurrentItems(ItemType.POTION);
                    state = FSM_State.BACKPACK;
                    return;
                case 'e': case 'у':
                    currentGame.setBackpackCurrentItems(ItemType.SCROLL);
                    state = FSM_State.BACKPACK;
            }
        }

    }

    private void handleBackpackInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) {
            state = FSM_State.GAME;
            return;
        }

        if (key.getKeyType() == KeyType.Character) {
            char c = Character.toLowerCase(key.getCharacter());

            if (c >= '0' && c <= '9') {
                // Выбор предмета по номеру (0-9)
                int itemIndex = c - '0';
                currentGame.selectBackpackItem(itemIndex);
            }

            switch (c) {
                case 'h': case 'р':
                    currentGame.setBackpackCurrentItems(ItemType.WEAPON);
                    state = FSM_State.BACKPACK;
                    return;
                case 'j': case 'о':
                    currentGame.setBackpackCurrentItems(ItemType.FOOD);
                    state = FSM_State.BACKPACK;
                    return;
                case 'k': case 'л':
                    currentGame.setBackpackCurrentItems(ItemType.POTION);
                    state = FSM_State.BACKPACK;
                    return;
                case 'e': case 'у':
                    currentGame.setBackpackCurrentItems(ItemType.SCROLL);
                    state = FSM_State.BACKPACK;
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
        Game loadedGame = LoadSaveData.quickLoad();
        if (loadedGame != null) {
            currentGame = loadedGame;
            state = FSM_State.GAME;
        }
    }

    private void saveGame() {
        LoadSaveData.quickSave(currentGame);
        System.out.println("Игра сохранена");
    }

    private void saveAndExit() {
        saveGame();
        running = false;
    }
}