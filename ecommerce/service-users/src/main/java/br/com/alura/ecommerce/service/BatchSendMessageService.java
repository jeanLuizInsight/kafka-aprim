package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.dto.UserDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {

    private final Connection connection;
    private final KafkaDispatcher<UserDTO> userDispatcher = new KafkaDispatcher<>();

    public BatchSendMessageService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        try {
            this.connection.createStatement().execute("create table Users (uuid varchar(200) primary key, email varchar(200))");
        } catch (SQLException e) {
            // Do not...vou ignorar pq não me importa se já foi criado.
        }
    }

    public static void main(String[] args) throws SQLException {
        var batchService = new BatchSendMessageService();
        try(var service = new KafkaService<String>(BatchSendMessageService.class.getSimpleName(),
                "SEND_MESSAGE_TO_ALL_USERS",
                batchService::parse,
                String.class)) {
            service.run();
        }
    }
    private void parse(ConsumerRecord<String, String> record) throws SQLException {
        System.out.println("----------------");
        System.out.println("processing new batch...");
        System.out.println("Topic " + record.value());
        List<UserDTO> users = this.getUsers();
        users.forEach(user -> {
            try {
                userDispatcher.send(record.value(),
                        user.getUuid(),
                        user);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<UserDTO> getUsers() throws SQLException {
        var results = this.connection.prepareStatement("select uuid from Users").executeQuery();
        List<UserDTO> users = new ArrayList<>();
        while (results.next()) {
            users.add(new UserDTO(results.getString(1)));
        }
        return users;
    }


}
