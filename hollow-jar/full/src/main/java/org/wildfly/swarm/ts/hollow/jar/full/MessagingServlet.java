package org.wildfly.swarm.ts.hollow.jar.full;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/messaging")
public class MessagingServlet extends HttpServlet {
    @Resource
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/queue/my-queue")
    private Queue queue;

    @Resource(lookup = "java:/jms/topic/my-topic")
    private Topic topic;

    @Inject
    private Result result;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String operation = req.getParameter("operation");

        if (operation == null || "".equals(operation)) {
            return;
        }

        if (operation.equals("sendQueue")) {
            result.clear();

            try (JMSContext context = connectionFactory.createContext()) {
                context.createProducer().send(queue, "1 in queue");
            }

            resp.getWriter().print("OK");
        } else if (operation.equals("sendTopic")) {
            result.clear();

            try (JMSContext context = connectionFactory.createContext()) {
                context.createProducer().send(topic, "2 in topic");
            }

            resp.getWriter().print("OK");
        } else if (operation.equals("results")) {
            result.getItems().forEach(resp.getWriter()::println);
        }
    }
}
