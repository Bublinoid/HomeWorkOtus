package ru.fisunov.http.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class ItemsWebApplication implements MyWebApplication {
    private String name;
    private List<Item> items;

    public ItemsWebApplication() {
        this.name = "Items Web Application";
    }

    @Override
    public void execute(Request request, OutputStream output) throws IOException {
        if (request.getMethod().equals("GET")) {
            handleGetRequest(output);
        } else if (request.getMethod().equals("POST")) {

            String requestBody = retrieveRequestBody(request);
            handlePostRequest(requestBody);
        }
    }

    private String retrieveRequestBody(Request request) throws IOException {
        InputStream inputStream = request.getInputStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString(StandardCharsets.UTF_8);
    }


    private void handleGetRequest(OutputStream outputStream) throws IOException {
        List<Item> itemsList = retrieveItemsFromDatabase();

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(itemsList);

        outputStream.write(("HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "\r\n" +
                jsonResponse).getBytes(StandardCharsets.UTF_8));
    }
    private List<Item> retrieveItemsFromDatabase() {
        List<Item> itemsList = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM items";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        long id = resultSet.getLong("id");
                        String title = resultSet.getString("title");
                        Item item = new Item(id, title);
                        itemsList.add(item);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return itemsList;
    }

    private void handlePostRequest(String requestBody) {
        Gson gson = new Gson();
        Item newItem = gson.fromJson(requestBody, Item.class);

        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO items (title) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newItem.getTitle());
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
