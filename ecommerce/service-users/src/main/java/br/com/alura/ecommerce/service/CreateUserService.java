package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceProvider;
import br.com.alura.ecommerce.dto.OrderDTO;
import br.com.alura.ecommerce.consumer.KafkaService;
import br.com.alura.ecommerce.utils.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CreateUserService implements ConsumerService<OrderDTO> {

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

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        var serviceProvider = new ServiceProvider(CreateUserService::new);
        var pool = Executors.newFixedThreadPool(1);
        serviceProvider.call();
    }

    @Override
    public void parse(ConsumerRecord<String, Message<OrderDTO>> record) throws SQLException {
        var message = record.value();
        System.out.println("----------------");
        System.out.println("processing new order, checking for new user...");
        System.out.println(record.key());
        System.out.println(record.value());
        var order = message.getPayload();
        if (this.isNewUser(order.getEmail())) {
            this.insertNewUser(order.getEmail());
        }
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }

    private void insertNewUser(final String email) throws SQLException {
        var insert = this.connection.prepareStatement("insert into Users(uuid, email) values (?,?)");
        insert.setString(1, UUID.randomUUID().toString());
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
