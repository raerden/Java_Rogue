package presentation;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import domain.Game;
import domain.Position;
import domain.level.Corridor;
import domain.level.Door;
import domain.level.Level;
import domain.level.Room;
import domain.player.Player;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class Presentation {
    private static final int ROOM_COUNT = 9;
    private static final int MAX_DOORS_IN_ROOM = 4;
    private static final int WINDOW_WIDTH = 100;
    private static final int WINDOW_HEIGHT = 40;
    private final Terminal terminal;
    private final Screen screen;

    private static final TextColor COLORBGROUND = TextColor.ANSI.BLACK;
    private static final TextColor COLORPLAYER = TextColor.ANSI.WHITE;
    private static final TextColor COLORBOUND = TextColor.ANSI.YELLOW;
    private static final TextColor COLORDOOR = TextColor.ANSI.YELLOW_BRIGHT;
    private static final TextColor COLORPASSAGE = TextColor.Factory.fromString("#555555");
    private static final TextColor COLORSTAIRS = TextColor.ANSI.BLUE;
    private static final String LEFTTOPBOUND = "╔";
    private static final String LEFTBOTBOUND = "╚";
    private static final String RIGHTTOPBOUND = "╗";
    private static final String RIGHTBOTBOUND = "╝";
    private static final String VERTBOUND = "║";
    private static final String HORIZBOUND = "═";
    private static final String HORIZDOOR = "━";
    private static final String VERTDOOR = "┃";
    private static final String PASSAGE = "░";
    private static final String ROOMFLOOR = ".";
    private static final String PLAYER = "@";
    private static final String STAIRSDOWN = "#";

    private final int MENU_WIDTH = 20;
    private final int MENU_HEIGHT = 18;
    private static final TextColor MENUBORDER = TextColor.ANSI.WHITE;
    private static final TextColor MENUBGROUND = TextColor.ANSI.BLACK_BRIGHT;

/*
╔═════╗
║     ┃░░░
║     ║
╚══━══╝
   ░

 ╔┓┏╦━━ ╦ ┓╔┓╔━━╗╔╗ ║┗┛║┗━╣┃║┃║╯╰║║║ ║┏┓║┏ ━ ╣┗ ╣┗╣╰╯║╠ ╣ ╚┛┗╩━━ ╩ ━╩━╩━━╝╚╝



 */

    public Presentation() throws IOException {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setInitialTerminalSize(new TerminalSize(WINDOW_WIDTH, WINDOW_HEIGHT)); // 100x40 символов

        // НАСТРОЙКА ШРИФТА - добавляем перед createTerminal()
        Font font = new Font("Monospaced", Font.BOLD, 18); // жирный, размер 24
        SwingTerminalFontConfiguration fontConfig =
                SwingTerminalFontConfiguration.newInstance(font);
        factory.setTerminalEmulatorFontConfiguration(fontConfig);

        try {
            terminal = factory.createTerminal();
            // Проверяем, является ли терминал SwingTerminalFrame
            if (terminal instanceof SwingTerminalFrame) {
                SwingTerminalFrame swingTerminal = (SwingTerminalFrame) terminal;
                // Центрируем окно на экране
                swingTerminal.setLocationRelativeTo(null); // null = относительно центра экрана
            }
            terminal.enterPrivateMode();
            screen = new TerminalScreen(terminal);
            //System.out.println("New Presentation");
        } catch (IOException e) {
            System.err.println("Failed to initialize terminal: " + e.getMessage());
            throw e;
        }
    }

    public void clear() {
        screen.clear();
    }

    public void start() throws IOException {
        screen.startScreen();
        clear();
        terminal.setCursorVisible(false);
    }

    public void end() throws IOException {
        screen.stopScreen();
        terminal.setCursorVisible(true);
        terminal.close();
    }

    public void displayGame(Game game) throws IOException {
        clear();
        printRooms(game.getCurrentLevel());                     // печать комнат
        printDoors(game.getCurrentLevel());                     // печать дверей
        printCorridors(game.getCurrentLevel());                 // печать коридоров
        putCh(STAIRSDOWN.charAt(0),                             // печать лестницы вниз
                game.getCurrentLevel().getStairsDown().getX(),
                game.getCurrentLevel().getStairsDown().getY(),
                COLORSTAIRS, COLORBGROUND);

        // ПЕЧАТЬ СУЩНОСТЕЙ

        printStatusBar(game);                                   // Печать строки состояния
        printPlayer(game.getPlayer());                          // Печать игрока

    }

    private void printStatusBar(Game game) throws IOException {
        //печать строки состояния
        // Level: 2  |  HP: 10(45)  |  Strength: 22(22)  |  Agility: 15  |  Gold: 33
        String levelStr = "Level " + game.getCurrentLevel().getLevelNumber();
        putString(levelStr, 1, WINDOW_HEIGHT - 1, MENUBGROUND, COLORBGROUND);
        int strLength = 1 + levelStr.length();
        putString(" | " , strLength , WINDOW_HEIGHT - 1, MENUBGROUND, COLORBGROUND);

        String playerHP = "HP: " + game.getPlayer().getCurrentHealth() + "/" + game.getPlayer().getMaxHealth();
        putString(playerHP, strLength + 3, WINDOW_HEIGHT - 1, TextColor.ANSI.RED_BRIGHT, COLORBGROUND);
        strLength += playerHP.length() + 3;
        putString(" | " , strLength , WINDOW_HEIGHT - 1, MENUBGROUND, COLORBGROUND);

        String playerStrength = "Strength: " + game.getPlayer().getStrength();
        putString(playerStrength, strLength + 3, WINDOW_HEIGHT - 1, TextColor.ANSI.GREEN_BRIGHT, COLORBGROUND);
        strLength += playerStrength.length() + 3;
        putString(" | " , strLength , WINDOW_HEIGHT - 1, MENUBGROUND, COLORBGROUND);

        String playerAgility = "Agility: " + game.getPlayer().getDexterity();
        putString(playerAgility, strLength + 3, WINDOW_HEIGHT - 1, TextColor.ANSI.CYAN, COLORBGROUND);
        strLength += playerAgility.length() + 3;
        putString(" | " , strLength , WINDOW_HEIGHT - 1, MENUBGROUND, COLORBGROUND);

        String playerGold = "Gold: " + game.getPlayer().getScore();
        putString(playerGold, strLength + 3, WINDOW_HEIGHT - 1, TextColor.ANSI.YELLOW_BRIGHT, COLORBGROUND);
    }


    private void printPlayer(Player player) throws IOException {
        if (player != null)
            putCh(PLAYER.charAt(0), player.getPosition().getX(), player.getPosition().getY(), COLORPLAYER, COLORBGROUND);
    }

    private void printCorridors(Level currentLevel) throws IOException {
        List<Corridor> corridors = currentLevel.getCorridors();
        for (int i = 0; i < corridors.size(); i++) {
            Corridor corridor = corridors.get(i);
            int x1 = corridor.getLeftCorner().getX();
            int y1 = corridor.getLeftCorner().getY();
            int x2 = corridor.getRightCorner().getX();
            int y2 = corridor.getRightCorner().getY();

            // рисуем включительно по x2 и y2
            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    putCh(PASSAGE.charAt(0), x, y, COLORPASSAGE, COLORBGROUND);
                }
            }
        }
    }

    private void printDoors(Level currentLevel) throws IOException {
        for (int i = 0; i < ROOM_COUNT; i++) {
            Door[] doors = currentLevel.getRoom(i).getDoors();

            if(currentLevel.getRoom(i).getUpperDoor() != null) {
                putCh(HORIZDOOR.charAt(0),
                        currentLevel.getRoom(i).getUpperDoor().getPosition().getX(),
                        currentLevel.getRoom(i).getUpperDoor().getPosition().getY(),
                        COLORDOOR, COLORBGROUND);
            }
            if(doors[1] != null) {
                putCh(VERTDOOR.charAt(0),
                        currentLevel.getRoom(i).getRigthDoor().getPosition().getX(),
                        currentLevel.getRoom(i).getRigthDoor().getPosition().getY(),
                        COLORDOOR, COLORBGROUND);
            }
            if(doors[2] != null) {
                putCh(HORIZDOOR.charAt(0),
                        currentLevel.getRoom(i).getBottomDoor().getPosition().getX(),
                        currentLevel.getRoom(i).getBottomDoor().getPosition().getY(),
                        COLORDOOR, COLORBGROUND);
            }
            if(doors[3] != null) {
                putCh(VERTDOOR.charAt(0),
                        currentLevel.getRoom(i).getLeftDoor().getPosition().getX(),
                        currentLevel.getRoom(i).getLeftDoor().getPosition().getY(),
                        COLORDOOR, COLORBGROUND);
            }
        }
    }

    private void printRooms(Level currentLevel) throws IOException {
        for (Room room : currentLevel.getRooms()) {
            printRoomBox(room.getLeftCorner(), room.getRightCorner(), COLORBOUND, COLORBGROUND);
        }
    }

    private void printRoomBox(Position leftCorner, Position rightCorner, TextColor color, TextColor bgcolor) throws IOException {
        int leftX = leftCorner.getX();
        int leftY = leftCorner.getY();
        int rightX = rightCorner.getX();
        int rightY = rightCorner.getY();

        // Рисуем верхнюю горизонтальную стену (с углами)
        for (int x = leftX + 1; x < rightX; x++) {
            putCh(HORIZBOUND.charAt(0), x, leftY, color, bgcolor);
        }

        // Рисуем нижнюю горизонтальную стену (с углами)
        for (int x = leftX + 1; x < rightX; x++) {
            putCh(HORIZBOUND.charAt(0), x, rightY, color, bgcolor);
        }

        // Рисуем левую вертикальную стену (с углами)
        for (int y = leftY + 1; y < rightY; y++) {
            putCh(VERTBOUND.charAt(0), leftX, y, color, bgcolor);
        }

        // Рисуем правую вертикальную стену (с углами)
        for (int y = leftY + 1; y < rightY; y++) {
            putCh(VERTBOUND.charAt(0), rightX, y, color, bgcolor);
        }

        // Рисуем углы
        putCh(LEFTTOPBOUND.charAt(0), leftX, leftY, color, bgcolor);     // ╔
        putCh(RIGHTTOPBOUND.charAt(0), rightX, leftY, color, bgcolor);  // ╗
        putCh(LEFTBOTBOUND.charAt(0), leftX, rightY, color, bgcolor);   // ╚
        putCh(RIGHTBOTBOUND.charAt(0), rightX, rightY, color, bgcolor); // ╝
    }

    private void displayLogo() throws IOException {
        putString("THE 'ROGUE' GAME", WINDOW_WIDTH / 2 - 8, 8, COLORDOOR, COLORBGROUND);
    }

    public void displayStartMenu() throws IOException {
        String[] menu = new String[]{
                "1 - Start Game",
                "2 - Load Game",
                "3 - Leaderboard",
                "ESC - Exit",
                " "," ",
                "Keyboard:",
                "ESC - Menu",
                "WASD - Move",
                "0-9 - Use item",
                "h - Weapons",
                "j - Foods",
                "k - Potions",
                "e - Scrolls",
                "p - Save Game"
        };

        int leftX = WINDOW_WIDTH / 2 - MENU_WIDTH / 2;
        int leftY = 10;
        int rightX = WINDOW_WIDTH / 2 + MENU_WIDTH / 2;
        int rightY = leftY + menu.length + 2;

        clearBox(new Position(leftX - 1, leftY - 3), new Position(rightX + 1, rightY + 1));
        displayLogo();

        Position leftCorner = new Position(leftX, leftY);
        Position rightCorner = new Position(rightX, rightY);
        printRoomBox(leftCorner, rightCorner, MENUBORDER, MENUBGROUND);
        for (int x = leftX + 1; x < rightX; x++) {
            for (int y = leftY + 1; y < rightY; y++) {
                putCh(' ', x, y, MENUBGROUND, MENUBGROUND);
            }
        }

        for (int i = 0; i < menu.length; i++) {
            putString(menu[i], leftX + 3, leftY + 2 + i, MENUBORDER, MENUBGROUND);
        }
    }

    private void clearBox(Position leftCorner, Position rightCorner) throws IOException {
        for (int x = leftCorner.getX(); x < rightCorner.getX() + 1; x++) {
            for (int y = leftCorner.getY(); y < rightCorner.getY() + 1; y++) {
                putCh(' ', x, y, TextColor.ANSI.BLACK, TextColor.ANSI.BLACK);
            }
        }
    }

    public String displayEnterNameDialog() throws IOException {
        int leftX = WINDOW_WIDTH / 2 - MENU_WIDTH / 2;
        int leftY = 12;
        int rightX = WINDOW_WIDTH / 2 + MENU_WIDTH / 2;
        int rightY = leftY + 2;

        Position leftCorner = new Position(leftX, leftY);
        Position rightCorner = new Position(rightX, rightY);
        printRoomBox(leftCorner, rightCorner, MENUBORDER, MENUBGROUND);
        for (int x = leftX + 1; x < rightX; x++) {
            for (int y = leftY + 1; y < rightY; y++) {
                putCh(' ', x, y, MENUBGROUND, MENUBGROUND);
            }
        }

        putString(" Enter name ", leftX + 4, leftY, MENUBORDER, MENUBGROUND);
        putString(" Esc: cancel ", leftX + 6, leftY + 2, MENUBORDER, MENUBGROUND);
        EnterName enterName = new EnterName(screen,  leftX + 2, leftY+1, 17);
        return enterName.show();
    }

    public void displayLeaderboard() {

    }

    public void putCh(char ch, int x, int y, TextColor color, TextColor bgColor) throws IOException {
        if (x < 0 || y < 0) return;
        // Получаем объект для рисования текста
        TextGraphics textGraphics = screen.newTextGraphics();
        // Устанавливаем цвет
        textGraphics.setForegroundColor(color);
        textGraphics.setBackgroundColor(bgColor);
        // Рисуем символ в нужной позиции
        textGraphics.putString(x, y, String.valueOf(ch));
    }

    public void putString(String str, int x, int y, TextColor color, TextColor bgColor) throws IOException {
        if (str == null || str.isEmpty() || x < 0 || y < 0) return;
        // Получаем объект для рисования текста
        TextGraphics textGraphics = screen.newTextGraphics();
        // Устанавливаем цвет
        textGraphics.setForegroundColor(color);
        textGraphics.setBackgroundColor(bgColor);
        // Рисуем строку в нужной позиции
        textGraphics.putString(x, y, str);
    }

    public void refresh() throws IOException {
        screen.refresh();
    }

    public InputProvider getScreen() {
        return screen;
    }
}
