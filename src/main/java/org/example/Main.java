package org.example;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private final static int port = 8989;
    public static Manager manager;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            manager = new Manager();
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                ) {
                    JsonReader reader = Json.createReader(in);
                    JsonObject request = reader.readObject();
                    reader.close();
                    manager.addPurchase(request.getString("title"), request.getString("date"), request.getInt("sum"));
                    JsonObject response = Json.createObjectBuilder()
                            .add("maxCategory", Json.createObjectBuilder()
                                    .add("category", manager.getMaxCategory().get().getKey())
                                    .add("sum", manager.getMaxCategory().get().getValue()))
                            .build();
                    out.println(response);
                } catch (IOException e) {
                    System.out.println("Не могу стартовать сервер");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}