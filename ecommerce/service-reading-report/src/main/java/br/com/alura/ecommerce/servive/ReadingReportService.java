package br.com.alura.ecommerce.servive;

import br.com.alura.ecommerce.dto.UserDTO;
import br.com.alura.ecommerce.service.KafkaDispatcher;
import br.com.alura.ecommerce.service.KafkaService;
import br.com.alura.ecommerce.utils.IO;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class ReadingReportService {

    private final KafkaDispatcher<UserDTO> orderDispatcher = new KafkaDispatcher<>();
    private static final Path SOURCE = new File("src/main/resources/report.txt").toPath();

    public static void main(String[] args) {
        var reportService = new ReadingReportService();
        try(var service = new KafkaService<UserDTO>(ReadingReportService.class.getSimpleName(),
                "USER_GENERATE_READING_REPORT",
                reportService::parse,
                UserDTO.class)) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, UserDTO> record) throws ExecutionException, InterruptedException, IOException {
        System.out.println("----------------");
        System.out.println("processing report for " + record.value());

        var user = record.value();
        var target = new File(user.getReportPath());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for " + user.getUuid());
        System.out.println("File created: " + target.getAbsolutePath());

    }
}