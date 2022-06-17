package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.dto.UserDTO;
import br.com.alura.ecommerce.utils.Message;
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
        try(var service = new KafkaService<>(BatchSendMessageService.class.getSimpleName(),
                "ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS",
                batchService::parse)) {
            service.run();
        }
    }
    private void parse(ConsumerRecord<String, Message<String>> record) throws SQLException {
        var message = record.value();
        System.out.println("----------------");
        System.out.println("processing new batch...");
        System.out.println("Topic " + message);
        List<UserDTO> users = this.getUsers();
        users.forEach(user -> {
            try {
                userDispatcher.send(message.getPayload(),
                        user.getUuid(),
                        user,
                        message.getId().continueWith(BatchSendMessageService.class.getSimpleName()));
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
