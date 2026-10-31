package demo;

import java.util.concurrent.atomic.AtomicInteger;

import framework.annotation.Authenticated;
import framework.annotation.Controller;
import framework.annotation.RoleAllowed;
import framework.annotation.Url;
import framework.util.ModAndView;
import framework.util.MySession;

@Controller
public class EmployeController {
    private final AtomicInteger invocationCount = new AtomicInteger();

    @Url(value = "/employe/form", method = "GET")
    public ModAndView form() {
        return new ModAndView("employe-form");
    }

    @Url(value = "/login", method = "GET")
    public ModAndView loginForm() {
        return new ModAndView("login-form");
    }

    @Url(value = "/login", method = "POST")
    public ModAndView login(String username, String role, MySession session) {
        session.set("connectedUser", username);
        session.set("roles", role);
        ModAndView result = new ModAndView("login-success");
        result.addValue("username", username);
        result.addValue("role", role);
        return result;
    }

    @Authenticated
    @RoleAllowed("prof")
    @Url(value = "/employe/save", method = "POST")
    public ModAndView save(Employe employe, MySession session) {
        int controllerInvocationCount = invocationCount.incrementAndGet();
        Integer sessionInvocationCount = session.get("saveCount", Integer.class);
        sessionInvocationCount = sessionInvocationCount == null ? 1 : sessionInvocationCount + 1;
        session.set("saveCount", sessionInvocationCount);

        ModAndView result = new ModAndView("employe-success");
        result.addValue("nom", employe.getNom());
        result.addValue("age", employe.getAge());
        result.addValue("salaire", employe.getSalaire());
        result.addValue("rectoCIN", employe.getRectoCIN());
        result.addValue("controllerInvocationCount", controllerInvocationCount);
        result.addValue("sessionInvocationCount", sessionInvocationCount);
        return result;
    }
}