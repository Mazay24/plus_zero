package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 1020;
    private static char[][] board = {{'1', '2', '3'}, {'4', '5', '6'}, {'7', '8', '9'}};
    private static boolean gameOver = false;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Сервер запущен. Ожидание двух игроков...");

        Socket player1 = serverSocket.accept();
        Socket player2 = serverSocket.accept();

        PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
        PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);

        BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));

        out1.println("Вы играете крестиками (X). Ожидайте ход соперника.");
        out2.println("Вы играете ноликами (O). Ожидайте ход соперника.");

        sendBoard(out1, out2);  // Отправка начального состояния поля игрокам

        char currentPlayer = 'X';

        while (!gameOver) {
            if (currentPlayer == 'X') {
                out1.println("Ваш ход (введите число от 1 до 9):");
                out2.println("Ожидание хода игрока X...");
                String move = in1.readLine();
                makeMove(move, 'X', out1, out2);
                currentPlayer = 'O';
            } else {
                out2.println("Ваш ход (введите число от 1 до 9):");
                out1.println("Ожидание хода игрока O...");
                String move = in2.readLine();
                makeMove(move, 'O', out1, out2);
                currentPlayer = 'X';
            }

            // Проверка на победителя или ничью
            if (checkWinner(out1, out2)) {
                gameOver = true;
            } else if (isBoardFull()) {
                declareDraw(out1, out2);
                gameOver = true;
            }
        }

        serverSocket.close();
    }

    private static void makeMove(String move, char player, PrintWriter out1, PrintWriter out2) {
        int cell = Integer.parseInt(move) - 1;
        int row = cell / 3;
        int col = cell % 3;

        if (board[row][col] != 'X' && board[row][col] != 'O') {
            board[row][col] = player;
            sendBoard(out1, out2);  // Отправка обновленного поля
        } else {
            if (player == 'X') {
                out1.println("Неверный ход. Попробуйте снова.");
            } else {
                out2.println("Неверный ход. Попробуйте снова.");
            }
        }
    }

    private static void sendBoard(PrintWriter out1, PrintWriter out2) {
        out1.println("|---|---|---|");
        out2.println("|---|---|---|");

        for (int i = 0; i < 3; i++) {
            StringBuilder line = new StringBuilder("| ");
            for (int j = 0; j < 3; j++) {
                line.append(board[i][j]).append(" | ");
            }
            out1.println(line);
            out2.println(line);

            if (i < 2) {
                out1.println("|-----------|");
                out2.println("|-----------|");
            }
        }
        out1.println("|---|---|---|");
        out2.println("|---|---|---|");
    }

    private static boolean checkWinner(PrintWriter out1, PrintWriter out2) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && (board[i][0] == 'X' || board[i][0] == 'O')) {
                declareWinner(board[i][0], out1, out2);
                return true;
            }

            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && (board[0][i] == 'X' || board[0][i] == 'O')) {
                declareWinner(board[0][i], out1, out2);
                return true;
            }
        }

        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && (board[0][0] == 'X' || board[0][0] == 'O')) {
            declareWinner(board[0][0], out1, out2);
            return true;
        }

        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && (board[0][2] == 'X' || board[0][2] == 'O')) {
            declareWinner(board[0][2], out1, out2);
            return true;
        }

        return false;
    }

    private static void declareWinner(char winner, PrintWriter out1, PrintWriter out2) {
        String message = "Победил игрок " + winner + "!";
        out1.println(message);
        out2.println(message);
    }

    private static void declareDraw(PrintWriter out1, PrintWriter out2) {
        String message = "Ничья! Поле полностью заполнено.";
        out1.println(message);
        out2.println(message);
    }

    private static boolean isBoardFull() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell != 'X' && cell != 'O') {
                    return false;
                }
            }
        }
        return true;
    }
}