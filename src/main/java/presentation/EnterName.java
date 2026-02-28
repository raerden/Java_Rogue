package presentation;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import java.io.IOException;

public class EnterName {
    private Screen screen;
    private StringBuilder inputText = new StringBuilder();
    private int x, y, width;
    private boolean active = true;
    private String result = null;
    private boolean canceled = false;

    public EnterName(Screen screen, int x, int y, int width) {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.width = width;
    }
    public String show() throws IOException {
        inputText.setLength(0); // очищаем предыдущий ввод
        active = true;
        canceled = false;

        while (active) {
            draw();
            screen.refresh();

            KeyStroke keyStroke = screen.readInput();

            if (keyStroke.getKeyType() == KeyType.Enter) {
                // Enter - завершаем ввод
                active = false;
                result = !inputText.isEmpty() ? inputText.toString() : null;
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                // Escape - отмена ввода
                active = false;
                canceled = true;
                result = null;
            } else if (keyStroke.getKeyType() == KeyType.Backspace) {
                // Backspace - удаляем последний символ
                if (!inputText.isEmpty()) {
                    inputText.deleteCharAt(inputText.length() - 1);
                }
            } else if (keyStroke.getKeyType() == KeyType.Character) {
                // Обычный символ - добавляем, если не превышаем ширину поля
                char c = keyStroke.getCharacter();
                // Разрешаем только печатные символы (буквы, цифры, пробел)
                if (Character.isLetterOrDigit(c) || c == ' ') {
                    if (inputText.length() < width) {
                        inputText.append(c);
                    }
                }
            }
        }

        return result;
    }

    private void draw() throws IOException {
        TextGraphics textGraphics = screen.newTextGraphics();

        // Рисуем черную рамку-подложку
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);

        // Заполняем поле пробелами (черный фон)
        for (int i = 0; i < width; i++) {
            textGraphics.putString(x + i, y, " ");
        }

        // Выводим введенный текст
        String displayText = inputText.toString();
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
        textGraphics.putString(x, y, displayText);

        int cursorPos = x + displayText.length();
        if (cursorPos < x + width) {
            textGraphics.setForegroundColor(TextColor.ANSI.BLACK);
            textGraphics.setBackgroundColor(TextColor.ANSI.WHITE);
            textGraphics.putString(cursorPos, y, " ");
        }
    }

    public boolean isCanceled() {
        return canceled;
    }
}
