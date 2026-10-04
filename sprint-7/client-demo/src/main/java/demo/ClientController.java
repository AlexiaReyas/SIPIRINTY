package demo;

import java.util.List;

import framework.annotation.Controller;
import framework.annotation.Url;
import framework.annotation.WebApi;

@Controller
public class ClientController {

    @WebApi
    @Url(value = "/clients", method = "GET")
    public List<Client> clients() {
        return List.of(
                new Client(1, "Alexia"),
                new Client(2, "Demo"));
    }
}