package br.com.alura.ecommerce.service;

import br.com.alura.ecommerce.dto.OrderDTO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<OrderDTO> dispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void destroy() {
        super.destroy();
        dispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var email = req.getParameter("email");
            var amount = req.getParameter("amount");
            var orderId = UUID.randomUUID().toString();
            var dto = new OrderDTO(orderId, new BigDecimal(amount), email);
            dispatcher.send("ECOMMERCE_NEW_ORDER", email, dto);
            var emailMsg = "Seu pedido está sendo processado.";
            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailMsg);

            System.out.println("Nova ordem enviada com sucesso!");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("Nova ordem enviada com sucesso!");
        } catch (ExecutionException | InterruptedException e) {
            throw new ServletException(e);
        }
    }
}
