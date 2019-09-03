package org.wildfly.swarm.ts.javaee8.jsf.ajax;

import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@Model
public class AjaxBean {
    @Inject
    private FacesContext context;

    private String output;

    public String getOutput() {
        return output;
    }

    public void eval() {
        context.getPartialViewContext()
                .getEvalScripts()
                .add("document.getElementById(\"output\").innerHTML = \"JavaScript from server\"");
    }

    public Object invoke() {
        output = "Ajax method invocation successful";
        return null;
    }
}
