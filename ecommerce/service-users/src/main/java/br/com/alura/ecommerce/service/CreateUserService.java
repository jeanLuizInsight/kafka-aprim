package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.dto.OrderDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private final Connection connection;

    public CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        try {
            this.connection.createStatement().execute("create table Users (uuid varchar(200) primary key, email varchar(200))");
        } catch (SQLException e) {
            // Do not...vou ignorar pq não me importa se já foi criado.
        }
    }

    public static void main(String[] args) throws SQLException {
        var userService = new CreateUserService();
        try(var service = new KafkaService<OrderDTO>(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                userService::parse,
                OrderDTO.class)) {
            service.run();
        }
    }
    private void parse(ConsumerRecord<String, OrderDTO> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("----------------");
        System.out.println("processing new order, checking for new user...");
        System.out.println(record.key());
        System.out.println(record.value());
        var order = record.value();
        if (this.isNewUser(order.getEmail())) {
            this.insertNewUser(order.getUserId(), order.getEmail());
        }
    }

    private void insertNewUser(final String uuid, final String email) throws SQLException {
        var insert = this.connection.prepareStatement("insert into Users(uuid, email) values (?,?)");
        insert.setString(1, uuid);
        insert.setString(2, email);
        insert.execute();
        System.out.println("Usuário uuid e " + email + " adicionado.");
    }

    private boolean isNewUser(final String email) throws SQLException {
        var exists = this.connection.prepareStatement("select uuid from Users where email = ? limit 1");
        var results = exists.executeQuery();
        return !results.next();
    }

}
