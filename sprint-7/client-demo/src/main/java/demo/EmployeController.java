package demo;

import framework.annotation.Controller;
import framework.annotation.Url;
import framework.util.ModAndView;

@Controller
public class EmployeController {

    @Url(value = "/employe/form", method = "GET")
    public ModAndView form() {
        return new ModAndView("employe-form");
    }

    @Url(value = "/employe/save", method = "POST")
    public ModAndView save(String nom, int age, double salaire) {
        ModAndView result = new ModAndView("employe-success");
        result.addValue("nom", nom);
        result.addValue("age", age);
        result.addValue("salaire", salaire);
        return result;
    }
}