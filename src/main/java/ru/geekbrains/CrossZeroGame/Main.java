package ru.geekbrains.CrossZeroGame;

import java.util.Random;
import java.util.Scanner;

public class Main {

     // Изображение хода игрока
    private static final char DOT_HUMAN = 'X';
    // Изображение хода компьютера
    private static final char DOT_AI = 'O';
    // Изображение пустой клетки
    private static final char DOT_EMPTY = '.';
    // Количество подряд идущих символов для выигрыша
    private static final int COUNT_WIN_POINTS = 4;
    // Scanner для ввода ходов игрока
    private static final Scanner SCANNER = new Scanner(System.in);
    // Random для случайного хода компьютера
    private static final Random random = new Random();
    // Поле для игры
    private static char[][] field;
    // Размер поля по X
    private static final int fieldSizeX = 9;
    // Размер поля по Y
    private static final int fieldSizeY  = 9;

    /**
     * Инициализация пустого поля
     */
    private static void init() {
        field = new char[fieldSizeY][fieldSizeX];
        for (int y = 0; y < fieldSizeX; y++) {
            for (int x = 0; x < fieldSizeX; x++) {
                field[y][x] = DOT_EMPTY;

            }
        }
    }

    /**
     * Вывод поля на экран
     */
    private static void printField() {

        System.out.print(" |");
        for (int i = 0; i < fieldSizeX; i++) {

            System.out.print(i != fieldSizeX - 1 ? i + 1 + "|" : i + 1);
        }
        System.out.println();
        for (int y = 0; y < fieldSizeY; y++) {
            System.out.print(y + 1 + "|");
            for (int x = 0; x < fieldSizeX; x++) {

                System.out.print(field[y][x] + "|");
            }
            System.out.println();
        }

    }

    /**
     * Проверка возмаожности хода
     * @param x - Координата X
     * @param y - Координата Y
     * @return Возвращает true, если ход возможен, false в противном случае
     */
    private static boolean isCellEmpty(int x, int y) {
        return field[y][x] == DOT_EMPTY;
    }

    /**
     * Проверка правильности ввода координат
     * @param x - Координата X
     * @param y - Координата Y
     * @return Возвращает true, если введенные данные в пределах диапазона, false в противном случае
     */
    private static boolean isCellValid(int x, int y) {
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }

    /**
     * Генерация очередного хода
     * @param isHuman определяет чей ход: true - человек, false - компьютер
     * @return Возвращает координаты хода
     */
    private static int[] nextTurn(boolean isHuman) {
        int[] step = new int[2];
        do {
            if (isHuman) {
                System.out.print(String.format("Введите координаты через пробел X (1-%d) и Y (1-%d) от 1 >> ", fieldSizeX, fieldSizeY));
                step[1] = SCANNER.nextInt() - 1;
                step[0] = SCANNER.nextInt() - 1;
            } else {
                // Проверка, может ли компьютер выиграть
                for (int y = 0; y < fieldSizeY; y++) {
                    for (int x = 0; x < fieldSizeX; x++) {
                        if (field[y][x] == DOT_EMPTY) {
                            field[y][x] = DOT_AI;
                            if (checkWin(false, x, y, COUNT_WIN_POINTS)) {
                                step[1] = x;
                                step[0] = y;
                                return step;
                            }
                            field[y][x] = DOT_EMPTY;
                        }
                    }
                }
                // Проверка может ли игрок выиграть следующим ходом
                for (int y = 0; y < fieldSizeY; y++) {
                    for (int x = 0; x < fieldSizeX; x++) {
                        if (field[y][x] == DOT_EMPTY) {
                            field[y][x] = DOT_HUMAN;
                            if (checkWin(true, x, y, COUNT_WIN_POINTS)) {
                                field[y][x] = DOT_AI;
                                step[1] = x;
                                step[0] = y;
                                return step;
                            }
                            field[y][x] = DOT_EMPTY;
                        }
                    }
                }
                // Проверка возможности n-1 подряд у игрока
                for (int y = 0; y < fieldSizeY; y++) {
                    for (int x = 0; x < fieldSizeX; x++) {
                        if (field[y][x] == DOT_EMPTY) {
                            field[y][x] = DOT_HUMAN;
                            if (checkWin(true, x, y, COUNT_WIN_POINTS - 1)) {
                                field[y][x] = DOT_AI;
                                step[1] = x;
                                step[0] = y;
                                return step;
                            }
                            field[y][x] = DOT_EMPTY;
                        }
                    }
                }
                // Проверка возможности сделать n-1 подряд у компьютера
                for (int y = 0; y < fieldSizeY; y++) {
                    for (int x = 0; x < fieldSizeX; x++) {
                        if (field[y][x] == DOT_EMPTY) {
                            field[y][x] = DOT_AI;
                            if (checkWin(false, x, y, COUNT_WIN_POINTS - 1)) {
                                field[y][x] = DOT_AI;
                                step[1] = x;
                                step[0] = y;
                                return step;
                            }
                            field[y][x] = DOT_EMPTY;
                        }
                    }
                }
                step[1] = random.nextInt(fieldSizeX);
                step[0] = random.nextInt(fieldSizeY);
            }
        } while (!isCellValid(step[1], step[0]) || !isCellEmpty(step[1], step[0]));
        if (isHuman) {
            field[step[0]][step[1]] = DOT_HUMAN;
        } else {
            field[step[0]][step[1]] = DOT_AI;
        }
        return step;
    }

    /**
     * Проверка завершения партии
     * @return Возвращает true, если ходов больше нет, false в противном случае
     */
    public static boolean isFinish() {
        for (int y = 0; y < fieldSizeY; y++) {
            for (int x = 0; x < fieldSizeX; x++) {
                if (field[y][x] == DOT_EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Проверка выигрыша
     * @param isHuman true - проверка выигрыша человека, false - компьютера
     * @param x - Координата X
     * @param y - Координата Y
     * @param winPoints - количество очков для выигрыша
     * @return Возвращает true, если победа, false в противном случае
     */
    private static boolean checkWin(boolean isHuman, int x, int y, int winPoints) {
        //  Горизонталь
        int count = 1;
        int step = -1;
        int currentX = x;
        int currentY = y;
        char сheck;
        boolean top = true;
        if (isHuman) {
            сheck = DOT_HUMAN;
        } else {
            сheck = DOT_AI;
        }
        do {
            if (currentX > 0 && field[currentY][currentX - 1] == сheck && top) {
                currentX += step;
                count++;
            } else {
                if (step < 0) currentX = x;
                top = false;
                step = 1;

                if (currentX < fieldSizeX - 1 && field[currentY][currentX + 1] == сheck) {
                    count++;
                    currentX += step;
                } else {
                    break;
                }
            }
        } while (count < winPoints);
        if (count == winPoints) return true;
        //  Вертикаль
        currentY = y;
        currentX = x;
        count = 1;
        step = -1;
        top = true;
        do {
            if (currentY > 0 && field[currentY - 1][currentX] == сheck & top) {
                count++;
                currentY += step;
            } else {
                if (step < 0) currentY = y;
                step = 1;
                top = false;
                if (currentY < fieldSizeY - 1 && field[currentY + 1][x] == сheck) {
                    count++;
                    currentY += step;
                } else break;
            }
        } while (count < winPoints);
        if (count == winPoints) return true;
        // Диагональ слева вниз
        currentY = y;
        currentX = x;
        count = 1;
        step = -1;
        top = true;
        do {
            if (currentY > 0 && currentX > 0 && field[currentY - 1][currentX - 1] == сheck && top) {
                count++;
                currentX += step;
                currentY += step;
            } else {
                if (step < 0) {
                    currentX = x;
                    currentY = y;
                }
                step = 1;
                top = false;
                if (currentY < fieldSizeY - 1 && currentX < fieldSizeX - 1 && field[currentY + 1][currentX + 1] == сheck) {
                    count++;
                    currentX++;
                    currentY++;
                } else break;
            }
        } while (count < winPoints);
        if (count == winPoints) return true;
        // Диагональ слева вверх
        currentY = y;
        currentX = x;
        count = 1;
        step = -1;
        top = true;
        do {
            if (currentX > 0 && currentY < fieldSizeY - 1 && field[currentY + 1][currentX - 1] == сheck && top) {
                count++;
                currentX += step;
                currentY -= step;
            } else {
                if (step < 0) {
                    currentX = x;
                    currentY = y;
                }
                step = 1;
                top = false;
                if (currentX < fieldSizeX - 1 && currentY > 0 && field[currentY - 1][currentX + 1] == сheck) {
                    count++;
                    currentX += step;
                    currentY -= step;
                } else break;
            }
        } while (count < winPoints);
        if (count == winPoints) return true;
        return false;
    }

    public static void main(String[] args) {
        init();
        printField();
        int[] currentStep;
        boolean isHuman = true;
        while (true) {
            currentStep = nextTurn(isHuman);
            printField();
            if (checkWin(isHuman, currentStep[1], currentStep[0], COUNT_WIN_POINTS)) {
                if (isHuman) {
                    System.out.println("Игрок победил");
                } else {
                    System.out.println("Компьютер победил");
                }
                break;
            }
            if (isFinish()) {
                System.out.println("Ничья");
                break;
            }
            System.out.println();
            isHuman = !isHuman;
        }

    }
}
