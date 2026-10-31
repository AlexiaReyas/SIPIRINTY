package demo;

import java.util.concurrent.atomic.AtomicInteger;

import framework.annotation.Controller;
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