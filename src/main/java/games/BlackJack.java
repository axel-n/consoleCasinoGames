package games;

import java.io.IOException;

public class BlackJack {

    private static int[] cards; // Основная колода
    private static int cursor; // Счётчик карт основной колоды

    private static int[][] playersCards; // карты игроков. Первый индекс - номер игрока
    private static int[] playersCursors; // курсоры карт игроков. Индекс - номер игрока

    private static int[] playersMoney = {100, 100};

    private static final int MAX_VALUE = 21;
    private static final int MAX_VALUE_FOR_AI = 16;
    private static final int MAX_CARDS_COUNT = 8;

    private static final int ID_USER = 0;
    private static final int ID_AI = 1;

    private static final int COUNT_CARDS_ON_START = 2;
    private static final int BET = 10;

    // для получения временных результов с картой
    private static String card;

    private static int value(int card) {
        switch (CardUtils.getPar(card)) {
            case JACK:
                return 2;
            case QUEEN:
                return 3;
            case KING:
                return 4;
            case SIX:
                return 6;
            case SEVEN:
                return 7;
            case EIGHT:
                return 8;
            case NINE:
                return 9;
            case TEN:
                return 10;
            case ACE:
            default:
                return 11;
        }
    }

    private static void initRound() {
        System.out.println("\nУ Вас " + playersMoney[0] + "$, у компьютера - " +
                playersMoney[1] + "$. Начинаем новый раунд!");
        cards = CardUtils.getShaffledCards();
        playersCards = new int[2][MAX_CARDS_COUNT];
        playersCursors = new int[]{0, 0};
        cursor = 0;
    }

    private static String addCard2Player(int player) {

        int indexCard = cards[cursor];

        playersCards[player][playersCursors[player]] = indexCard;

        playersCursors[player]++;
        cursor++;

        return CardUtils.toString(indexCard);
    }

    // метод, который будет суммировать очки игрока
    static int sum(int player) {

        int sum = 0;

        for (int i = 0; i < playersCursors[player]; i++) {

            sum += value(playersCards[player][i]);
        }

        return sum;
    }

    // получение суммы игрока, если она валидная
    // 0, если превысили порог MAX_VALUE
    static int getFinalSum(int player) {
        int sum = sum(player);

        return sum <= MAX_VALUE ? sum : 0;
    }

    static boolean existWinner() {

        // если могут сделать ставку
        if (((playersMoney[ID_AI] - BET) >= 0) && ((playersMoney[ID_USER] - BET) >= 0)) {
            return false;
        } else {
            return true;
        }

    }

    public static void main(String... __) throws IOException {


        while (!existWinner()) {

            initRound();

            // ход человека
            // по правилами, 2 раза должны давать карту
            for (int j = 0; j < COUNT_CARDS_ON_START; j++) {
                card = addCard2Player(ID_USER);
                System.out.printf("Вам выпала карта %s%n", card);
            }

            for (int j = 0; j < MAX_CARDS_COUNT; j++) {
                int sum = sum(ID_USER);

                if (sum <= MAX_VALUE) {

                    if (Choice.confirm("Берём ещё?")) {

                        String card = addCard2Player(ID_USER);
                        System.out.printf("Вам выпала карта %s%n", card);
                        continue;

                    } else {
                        // Пользователь не хочет больше брать
                        break;
                    }
                }
            }

            // ход компьютера
            // по правилами, 2 раза должны давать карту
            for (int j = 0; j < COUNT_CARDS_ON_START; j++) {
                card = addCard2Player(ID_AI);
                System.out.printf("Компьютеру выпала карта %s%n", card);
            }

            for (int j = 0; j < MAX_CARDS_COUNT; j++) {

                int sum = sum(ID_AI);

                if (sum <= MAX_VALUE_FOR_AI) {

                    String card = addCard2Player(ID_AI);
                    System.out.printf("Компьютер решил взять ещё и ему выпала карта %s%n", card);
                    continue;

                }
            }

            // считаем итог
            int sum1 = getFinalSum(ID_USER);
            int sum2 = getFinalSum(ID_AI);

            System.out.printf("Сумма ваших очков - %d, компьютера - %d%n", sum1, sum2);

            if (sum1 > sum2) {
                System.out.printf("Вы выиграли раунд! Получаете %d$%n", BET);
                playersMoney[ID_USER] += BET;
                playersMoney[ID_AI] -= BET;
            } else if (sum1 == sum2) {
                System.out.printf("Ничья! Все остаются при своих%n");
            } else {
                System.out.printf("Вы проиграли раунд! Теряете %d$%n", BET);
                playersMoney[ID_USER] -= BET;
                playersMoney[ID_AI] += BET;
            }
        }

        if (playersMoney[ID_USER] > 0)
            System.out.println("Вы выиграли! Поздравляем!");
        else
            System.out.println("Вы проиграли. Соболезнуем...");
    }
}