package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client2 {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 1020);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        String serverMessage;
        while ((serverMessage = input.readLine()) != null) {
            System.out.println(serverMessage);

            if (serverMessage.contains("Ваш ход")) {
                String move = userInput.readLine();
                output.println(move);
            }

            if (serverMessage.contains("Победил")) {
                break;
            }
        }

        socket.close();
    }
}
