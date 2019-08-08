package org.wildfly.swarm.ts.javaee8.jsonp11;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/jsonp/pointer")
public class PointerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonController controller = new JsonController();
        String operation = req.getParameter("operation");
        String path = req.getParameter("path");
        String value = req.getParameter("value");
        String result = "initial empty value";
        switch (operation) {
            case "contains-value": {
                result = controller.pointerContainsValue(value);
                break;
            }
            case "get-value": {
                result = controller.pointerGetValue(value);
                break;
            }
            case "add": {
                result = controller.pointerAdd(path, value);
                break;
            }
            case "replace": {
                result = controller.pointerReplace(path, value);
                break;
            }
            case "remove": {
                result = controller.pointerRemove(path);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: operation=" + operation + " path=" + path + " value=" + value);
        }
        resp.setContentType("text/plain");
        resp.getWriter().print(result);
    }
}
