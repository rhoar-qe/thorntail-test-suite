package org.wildfly.swarm.ts.javaee8.jsonp11;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/jsonp/patch")
public class PatchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonController controller = new JsonController();
        String operation = req.getParameter("operation");
        String path = req.getParameter("path");
        String value = req.getParameter("value");
        String result = "initial empty value";
        switch (operation) {
            case "add": {
                result = controller.patchAdd(path, value);
                break;
            }
            case "remove": {
                result = controller.patchRemove(path);
                break;
            }
            case "move": {
                result = controller.patchMove(path, value);
                break;
            }
            case "copy": {
                result = controller.patchCopy(path, value);
                break;
            }
            case "replace": {
                result = controller.patchReplace(path, value);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value:  operation=" + operation + " path=" + path + " value=" + value);
        }
        resp.setContentType("text/plain");
        resp.getWriter().print(result);
    }
}
