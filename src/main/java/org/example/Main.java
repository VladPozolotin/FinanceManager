package org.example;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private final static int port = 8989;
    public static Manager manager;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            File data = new File("data.bin");
            manager = Manager.loadFromBinFile(data);
            if (manager == null) {
                manager = new Manager();
            }
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
                            .add("maxYearCategory", Json.createObjectBuilder()
                                    .add("category", manager.getMaxYearCategory().get().getKey())
                                    .add("sum", manager.getMaxYearCategory().get().getValue()))
                            .add("maxMonthCategory", Json.createObjectBuilder()
                                    .add("category", manager.getMaxMonthCategory().get().getKey())
                                    .add("sum", manager.getMaxMonthCategory().get().getValue()))
                            .add("maxDayCategory", Json.createObjectBuilder()
                                    .add("category", manager.getMaxDayCategory().get().getKey())
                                    .add("sum", manager.getMaxDayCategory().get().getValue()))
                            .build();
                    out.println(response);
                    manager.saveBin(data, manager);
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