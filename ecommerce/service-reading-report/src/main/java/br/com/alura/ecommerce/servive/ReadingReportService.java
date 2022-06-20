package br.com.alura.ecommerce.servive;

import br.com.alura.ecommerce.dto.UserDTO;
import br.com.alura.ecommerce.service.KafkaService;
import br.com.alura.ecommerce.utils.IO;
import br.com.alura.ecommerce.utils.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class ReadingReportService {

    private static final Path SOURCE = new File("src/main/resources/report.txt").toPath();

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        var reportService = new ReadingReportService();
        try(var service = new KafkaService<>(ReadingReportService.class.getSimpleName(),
                "ECOMMERCE_USER_GENERATE_READING_REPORT",
                reportService::parse)) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<UserDTO>> record) throws ExecutionException, InterruptedException, IOException {
        var message = record.value();
        System.out.println("----------------");
        System.out.println("processing report for " + record.value());

        var user = message.getPayload();
        var target = new File(user.getReportPath());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for " + user.getUuid());
        System.out.println("File created: " + target.getAbsolutePath());

    }
}
