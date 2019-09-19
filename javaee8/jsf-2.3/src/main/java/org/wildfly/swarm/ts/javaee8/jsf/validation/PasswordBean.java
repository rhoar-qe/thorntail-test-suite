package org.wildfly.swarm.ts.javaee8.jsf.validation;

import javax.enterprise.inject.Model;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Model
@Password(groups = PasswordValidationGroup.class)
public class PasswordBean implements PasswordHolder, Cloneable {
    private String password1 = "";

    private String password2 = "";

    @Override
    @NotNull(groups = PasswordValidationGroup.class)
    @Size(min = 8, max = 20, groups = PasswordValidationGroup.class, message = "password has wrong size")
    public String getPassword1() {
        return password1;
    }

    @Override
    @NotNull(groups = PasswordValidationGroup.class)
    @Size(min = 8, max = 20, groups = PasswordValidationGroup.class, message = "password has wrong size")
    public String getPassword2() {
        return password2;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        PasswordBean other = (PasswordBean) super.clone();
        other.setPassword1(this.getPassword1());
        other.setPassword2(this.getPassword2());
        return other;
    }
}
