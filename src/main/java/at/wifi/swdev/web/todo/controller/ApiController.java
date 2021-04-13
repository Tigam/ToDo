package at.wifi.swdev.web.todo.controller;

import at.wifi.swdev.web.todo.model.ToDoItem;
import at.wifi.swdev.web.todo.service.MockupService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @Autowired
    private MockupService svc;

    @GetMapping("/api/data")
    public Map<String, Object> doExportJson() {
        Map<String, Object> result = new HashMap<>();
        result.put("vorname", "Thomas");
        result.put("nachname", "Knapp");
        result.put("code", 42);
        result.put("now", new Date());

        return result;
    }

    @GetMapping("/api/todos")
    public List<ToDoItem> getTodoItems() {
        List<ToDoItem> items = svc.getTodoItems();
        return items;
    }
}
