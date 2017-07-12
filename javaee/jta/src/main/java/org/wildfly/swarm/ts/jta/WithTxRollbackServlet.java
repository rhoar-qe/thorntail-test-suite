package org.wildfly.swarm.ts.jta;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;
import java.io.IOException;

@WebServlet("/with-tx-rollback")
public class WithTxRollbackServlet extends HttpServlet {
    @Resource
    private UserTransaction tx;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.getWriter().println(tx.getStatus());
            tx.begin();
            resp.getWriter().println(tx.getStatus());
            tx.setRollbackOnly();
            resp.getWriter().println(tx.getStatus());
            tx.rollback();
            resp.getWriter().print(tx.getStatus());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
