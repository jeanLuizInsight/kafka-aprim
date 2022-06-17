package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.utils.CorrelationID;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class GenerateAllReportsServlet extends HttpServlet {

    private final KafkaDispatcher<String> dispatcher = new KafkaDispatcher<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void destroy() {
        super.destroy();
        dispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            dispatcher.send("ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS",
                    "ECOMMERCE_USER_GENERATE_READING_REPORT",
                    "ECOMMERCE_USER_GENERATE_READING_REPORT",
                    new CorrelationID(GenerateAllReportsServlet.class.getSimpleName()));
            System.out.println("Enviado geração de relatório de todos os usuários.!");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("Requisição de relatório gerada com sucesso!");
        } catch (ExecutionException | InterruptedException e) {
            throw new ServletException(e);
        }
    }
}
