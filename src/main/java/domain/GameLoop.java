package domain;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import datalayer.GameStats;
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
    private FSM_State prev_state;

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
                // Сохраняем статистику при смерти
                saveGameStatistics("death");
                // Показываем сообщение о смерти
                presentation.showDeathMessage(currentGame.getPlayer().getName());
                // Очищаем текущую игру
                currentGame = null;
                state = FSM_State.START;
            }
            else if (currentGame != null && currentGame.getLevel() != null &&(currentGame.getLevel().getLevelNumber() > 20 ||
                    (currentGame.getLevel().getLevelNumber() == 20 &&
                            currentGame.getPlayer().getPosition().equal(currentGame.getLevel().getStairsDown())))) {
                // Статистика уже сохранена в checkStairsDown, но можно добавить доп. обработку
                presentation.showVictoryMessage(currentGame.getPlayer().getName());
                currentGame = null;

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
                presentation.clear();
                presentation.displayStartMenu();
                break;
            case PAUSE:
                presentation.displayPauseMenu();
                break;
            case GAME:
                presentation.clear();
                presentation.displayGame(currentGame);
                break;
            case BACKPACK:
                presentation.displayBackpack(currentGame);
                break;
            case LEADERS:
                presentation.clear();
                presentation.displayLeaderboard(LoadSaveData.getTopStats());
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

    private void saveGameStatistics(String result) {
        if (currentGame != null && currentGame.getGameStats() != null) {
            GameStats stats = currentGame.getGameStats();
            stats.setResult(result);
            stats.setLevel(currentGame.getLevel().getLevelNumber());
            LoadSaveData.saveStatistics(stats);
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
                    String name = presentation.displayEnterNameDialog();
                    if (name != null && !name.isEmpty()) {
                        currentGame = new Game(name);
                        currentGame.generateLevel(1);
                        state = FSM_State.GAME;
                    }
                    break;
                case '2':
                    loadGame();
                    break;
                case '3':
                    prev_state = FSM_State.START;
                    state = FSM_State.LEADERS;
                    break;
            }
        }
    }

    private void handlePauseInput(KeyStroke key) throws IOException {
        if (key.getKeyType() == KeyType.Escape) {
            // Сохраняем статистику при выходе
            saveGameStatistics("quit");
            running = false;
            return;
        }

        if (key.getKeyType() == KeyType.Character) {
            char c = key.getCharacter();
            switch (c) {
                case '1':
                    state = FSM_State.GAME;
                    break;
                case '2':
                    String name = presentation.displayEnterNameDialog();
                    if (name != null && !name.isEmpty()) {
                        currentGame = null;
                        currentGame = new Game(name);
                        currentGame.generateLevel(1);
                        state = FSM_State.GAME;
                    }
                    break;
                case '3':
                    saveGame();
                    break;
                case '4':
                    loadGame();
                    break;
                case '5':
                    prev_state = FSM_State.GAME;
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
            state = prev_state;
        }
    }

    private void handleDefaultInput(KeyStroke key) {
        System.out.println("Необработанная клавиша: " + key);
    }

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
}