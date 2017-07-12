package org.wildfly.swarm.ts.javaee.bean.validation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

@WebServlet("/")
public class HelloServlet extends HttpServlet {
    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        {
            Greeting g = new Greeting("Hello!");
            Set<ConstraintViolation<Greeting>> violations = validator.validate(g);
            resp.getWriter().println("Violations: " + violations.size());
        }

        {
            Greeting g = new Greeting("Hello from servlet with Bean Validation, this message is too long!!!");
            Set<ConstraintViolation<Greeting>> violations = validator.validate(g);
            resp.getWriter().println("Violations: " + violations.size());
        }
    }
}
