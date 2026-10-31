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
    public ModAndView save(Employe employe) {
        ModAndView result = new ModAndView("employe-success");
        result.addValue("nom", employe.getNom());
        result.addValue("age", employe.getAge());
        result.addValue("salaire", employe.getSalaire());
        result.addValue("rectoCIN", employe.getRectoCIN());
        return result;
    }
}