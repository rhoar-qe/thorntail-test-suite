package org.wildfly.swarm.ts.wildfly.hibernate.validator;

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
            CreditCard g = new CreditCard("4111111111111111");
            Set<ConstraintViolation<CreditCard>> violations = validator.validate(g);
            resp.getWriter().println("Violations: " + violations.size());
        }

        {
            CreditCard g = new CreditCard("1234567890");
            Set<ConstraintViolation<CreditCard>> violations = validator.validate(g);
            resp.getWriter().println("Violations: " + violations.size());
        }
    }
}
