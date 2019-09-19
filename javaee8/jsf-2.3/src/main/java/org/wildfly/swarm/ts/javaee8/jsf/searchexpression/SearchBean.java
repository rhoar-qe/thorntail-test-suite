package org.wildfly.swarm.ts.javaee8.jsf.searchexpression;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.search.SearchExpressionContext;
import javax.faces.component.search.SearchExpressionHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PreRenderViewEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Named
@ApplicationScoped
public class SearchBean {
    @Inject
    private FacesContext context;

    private String success;

    public String getSuccess() {
        return success;
    }

    public void handleEvent(PreRenderViewEvent event) {
        Map<String, String> map = new HashMap<>();
        map.put(":form:@parent", "body");
        map.put(":form:panel:@child(0)", "firstButton");
        map.put(":form:panel:@child(1)", "secondButton");
        map.put(":form:panel:@id(firstButton)", "firstButton");
        map.put(":form:panel:firstButton:@next", "secondButton");
        map.put(":form:panel:secondButton:@previous", "firstButton");
        map.put(":form:panel:firstButton:@namingcontainer", "form");

        AtomicInteger counter = new AtomicInteger(0);
        SearchExpressionContext searchContext = SearchExpressionContext.createSearchExpressionContext(context, context.getViewRoot());
        SearchExpressionHandler search = context.getApplication().getSearchExpressionHandler();
        for (String componentPath : map.keySet()) {
            search.resolveComponent(searchContext, componentPath, (ignored, foundComponent) -> {
                if (foundComponent.getId().equals(map.get(componentPath))) {
                    counter.incrementAndGet();
                }
            });
        }

        success = (counter.get() == map.keySet().size()) ? "success" : "failure";
    }
}
