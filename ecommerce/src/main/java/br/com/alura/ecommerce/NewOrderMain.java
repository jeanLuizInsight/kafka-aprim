package br.com.alura.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static java.lang.System.getProperties;

/**
 * Producer para nova ordem no ecommerce
 */
public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var producer = new KafkaProducer<String, String>(getProperties());
        var key = UUID.randomUUID().toString();
        var value = "132123,67523,345654647";
        // registro: topico, chave, valor
        var record = new ProducerRecord<>("ECOMMERCE_NEW_ORDER", key, value);
        var email = "Seu pedido está sendo processado.";
        var emailRecord = new ProducerRecord<>("ECOMMERCE_SEND_EMAIL", key, email);
        // o método send retorna um Future, ou seja, não blocante sem esperar a execução terminar
        // para tal utilizar o get()
        // producer.send(record);
        // variação com callback
        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println("mensagem enviada com sucesso: " + data.topic() +
                    "::partition " + data.partition() +
                    "/ offset " + data.offset());
        };
        // produzindo notificação para nova ordem
        producer.send(record, callback).get();
        // produzindo notificações para email
        producer.send(emailRecord, callback).get();

    }

    /**
     * Propriedades para registro do producer.
     * @return
     */
    private static Properties getProperties() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }
}