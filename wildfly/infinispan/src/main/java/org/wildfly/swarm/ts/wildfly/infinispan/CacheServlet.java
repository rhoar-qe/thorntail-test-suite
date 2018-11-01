package org.wildfly.swarm.ts.wildfly.infinispan;

import org.infinispan.Cache;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/")
public class CacheServlet extends HttpServlet {
    @Resource(lookup = "java:jboss/infinispan/cache/test-cache-container/test-cache")
    private Cache cache;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String key = req.getParameter("key");
        Object value = cache.get(key);
        if (value == null) {
            resp.sendError(404);
        } else {
            resp.getWriter().print(value);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String key = req.getParameter("key");
        String value = req.getParameter("value");
        cache.put(key, value);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String key = req.getParameter("key");
        cache.remove(key);
    }
}
