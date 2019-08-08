package org.wildfly.swarm.ts.javaee8.jsonp11;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/jsonp/merge")
public class MergeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonController controller = new JsonController();
        String feature = req.getParameter("feature");
        String path = req.getParameter("path");
        String value = req.getParameter("value");
        String result = "initial empty value";
        switch (feature) {
            case "merge-patch-minimal": {
                result = controller.mergePatch(true);
                break;
            }
            case "merge-patch-normal": {
                result = controller.mergePatch(false);
                break;
            }
            case "merge-diff": {
                result = controller.mergeDiff();
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: feature=" + feature +
                        " path=" + path + " value=" + value);
        }
        resp.setContentType("text/plain");
        resp.getWriter().print(result);
    }
}
