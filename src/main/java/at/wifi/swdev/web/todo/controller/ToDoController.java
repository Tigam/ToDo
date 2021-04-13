package at.wifi.swdev.web.todo.controller;

import at.wifi.swdev.web.todo.model.ToDoItem;
import at.wifi.swdev.web.todo.service.MockupService;
import at.wifi.swdev.web.todo.service.TodoService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ToDoController {

    @Autowired
    private MockupService svc;
    //private TodoService svc;
    private static final Logger LOG = Logger.getLogger(ToDoController.class.getName());

    public ToDoController() {
    }

    @PostConstruct
    public void init() {
//    items = new ArrayList<>();
//    ToDoItem itemA = new ToDoItem("Erster Eintrag", "Das ist mein erster Eintrag");
//    items.add(itemA);
//    ToDoItem itemB = new ToDoItem("Zweiter Eintrag", "Das ist mein zweiter Eintrag");
//    items.add(itemB);
//    
//    for (int i = 1; i <= 50; i++) {
//      items.add(new ToDoItem("Titel_" + i, "Beschreibung_" + i));
//    }
    }

    @GetMapping({"/", "/list"})
    public String showList(Model model) {

        List<ToDoItem> items = svc.getTodoItems();
//        int closedCounter = 0;
//        for (ToDoItem item : items) {
//            if (item.isClosed()) {
//                closedCounter++;
//            }
//        }
        int closedCounter = (int)items
                .stream()
                //.parallel()
                .filter(ToDoItem::isClosed) //.filter(item -> item.isClosed())
                //.peek(System.out::println)
                .count();
        int openCounter = items.size() - closedCounter;

        model.addAttribute("items", items);
        model.addAttribute("suche", "");
        model.addAttribute("closedCounter", closedCounter);
        model.addAttribute("openCounter", openCounter);
        return "list";
    }
    
    @PostMapping("/list")
    public String doSearch(Model model, @RequestParam String suche) {
        LOG.info("Suchparameter: " + suche);
        List<ToDoItem> allItems = svc.getTodoItems();
        List<ToDoItem> items = new ArrayList<>();

        String sucheLower = suche.trim().toLowerCase();
        for (ToDoItem item : allItems) {
            if (item.getTitle().toLowerCase().contains(sucheLower) ||
                    item.getText().toLowerCase().contains(sucheLower)) {
                items.add(item);
            }
        }
        
        int closedCounter = (int)items
                .stream()
                .filter(ToDoItem::isClosed)
                .count();
        int openCounter = items.size() - closedCounter;

        model.addAttribute("items", items);
        model.addAttribute("suche", suche);
        model.addAttribute("closedCounter", closedCounter);
        model.addAttribute("openCounter", openCounter);
        return "list";
    }
    

    @GetMapping("/edit")
    public String showEdit(Model model, @RequestParam String id) {
        try {
            long idForItem = Long.parseLong(id);
            Optional<ToDoItem> itemOpt = svc.getToDoItem(idForItem);
            if (itemOpt.isPresent()) {
                // mache was...
                model.addAttribute("item", itemOpt.get());
                return "edit";
            } else {
                return "redirect:/list";
            }
        } catch (NumberFormatException nfe) {
            System.err.println("Fehler beim Parsen der ID!");
            return "redirect:/list";
        }

    }

    @PostMapping("/edit")
    public String doEdit(
            Model model,
            @Valid @ModelAttribute ToDoItem item,
            BindingResult bindingResult,
            RedirectAttributes redirectAttr) {
        // Validierung
        List<String> errMsgs = new ArrayList<>();
        validateToDoItem(item, errMsgs, model, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("item", item);
            System.out.println("Validierungsfehler vorhanden!");
            return "edit";
        }

        // Hinzufügen
        svc.editTodoItem(item);
        redirectAttr.addFlashAttribute("infoMsg", "Aufgabe erfolgreich geändert.");
        return "redirect:/list"; // templates/list.html Redirect (Post-Get Redirect Pattern)
    }

    private void validateToDoItem(ToDoItem item, List<String> errMsgs, Model model, BindingResult bindingResult) {
        if (Objects.nonNull(item.getTitle())
                && item.getTitle().trim().length() < 3) {
            System.out.println("Fehler bei der Validierung (title)! " + item.getText());
            errMsgs.add("Fehler bei der Eingabe beim Titel! Mindestens 3 Buchstaben beim Titel!");
            model.addAttribute("errMsgTitle", "Mindestens 3 Buchstaben!");
            bindingResult.rejectValue("title", "error.title", "Mindestens 3 Buchstaben eingeben!");
            //bindingResult.addError(new ObjectError("title", "Mindestens 3 Buchstaben eingeben!"));
            bindingResult.addError(new FieldError("title", "item",
                    "Mindestens 3 Buchstaben eingeben!"));
        }
        if (Objects.nonNull(item.getText())
                && item.getText().trim().length() < 4) {
            System.out.println("Fehler bei der Validierung (text)! " + item.getText());
            errMsgs.add("Fehler bei der Eingabe beim Text! Mindestens 4 Buchstaben beim Text!");
            model.addAttribute("errMsgText", "Mindestens 4 Buchstaben!");
            bindingResult.addError(new FieldError("text", "minerror",
                    "Mindestens 4 Buchstaben eingeben!"));
        }
        if (Objects.nonNull(item.getTitle())
                && item.getTitle().toLowerCase().contains("klo")) {
            System.out.println("Fehler bei der Validierung (title)! " + item.getTitle());
            item.setTitle("");
            errMsgs.add("Fehler bei der Eingabe beim Titel! Klopapier ist genug da!");
            bindingResult.addError(new ObjectError("title", "Klopapier ist genug da!"));

            model.addAttribute("errMsgTitle", "Klopapier ist genug da!");
        }
        if (Objects.isNull(item.getTodoDate())) {
            System.out.println("Fehler bei der Validierung (todoData)! " + item.getTitle());
            item.setTitle("");
            errMsgs.add("Fehler bei der Eingabe beim Datum!");

            model.addAttribute("errMsgTodoDate", "Datumsformat für Datum: dd.mm.jjjj");
        }
    }

    @GetMapping("/new")
    public String showNew(Model model) {
        model.addAttribute("item", new ToDoItem());
        return "new"; // templates/new.html
    }

    @PostMapping("/new")
    public String doNew(
            Model model,
            @Valid @ModelAttribute ToDoItem item,
            BindingResult bindingResult,
            RedirectAttributes redirectAttr) {
        // Validierung
        List<String> errMsgs = new ArrayList<>();
        validateToDoItem(item, errMsgs, model, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("item", item);
            System.out.println("Validierungsfehler vorhanden!");
            return "new";
        }

        // Hinzufügen
        item = svc.addTodoItem(item);
        System.out.println("Todo Item gespeichert: " + item.toString());
        redirectAttr.addFlashAttribute("infoMsg", "Aufgabe erfolgreich angelegt.");
        //model.addAttribute("items", this.items); // Wird im Redirect über GET /list gesetzt!
        //return "list"; // templates/list.html Redirect (Post-Get Redirect Pattern)
        return "redirect:/list"; // templates/list.html Redirect (Post-Get Redirect Pattern)
    }

    /**
     * Code für POST Validierung Eigenentwicklung
     *
     * @param model
     * @param title
     * @return
     */
//  @PostMapping("/new")
//  public String doNew(
//          Model model,
//          RedirectAttributes redirectAttr,
//          @ModelAttribute ToDoItem item) {
//    // Validierung
//    List<String> errMsgs = new ArrayList<>();
//    if (Objects.nonNull(item.getTitle())
//            && item.getTitle().trim().length() < 3) {
//      System.out.println("Fehler bei der Validierung (title)! " + item.getText());
//      errMsgs.add("Fehler bei der Eingabe beim Titel! Mindestens 3 Buchstaben beim Titel!");
//      model.addAttribute("errMsgTitle", "Mindestens 3 Buchstaben!");
//    }
//    if (Objects.nonNull(item.getText())
//            && item.getText().trim().length() < 3) {
//      System.out.println("Fehler bei der Validierung (text)! " + item.getText());
//      errMsgs.add("Fehler bei der Eingabe beim Text! Mindestens 3 Buchstaben beim Text!");
//      model.addAttribute("errMsgText", "Mindestens 3 Buchstaben!");
//    }
//    if (Objects.nonNull(item.getTitle())
//            && item.getTitle().toLowerCase().contains("klo")) {
//      System.out.println("Fehler bei der Validierung (title)! " + item.getTitle());
//      item.setTitle("");
//      errMsgs.add("Fehler bei der Eingabe beim Titel! Klopapier ist genug da!");
//      model.addAttribute("errMsgTitle", "Klopapier ist genug da!");
//    }
//    if (!errMsgs.isEmpty()) {
//      model.addAttribute("errMsgs", errMsgs);
//      model.addAttribute("item", item);
//      return "new";
//    }
//
//    // Hinzufügen
//    this.items.add(item);
//    redirectAttr.addFlashAttribute("infoMsg", "Aufgabe erfolgreich angelegt.");
//    //model.addAttribute("items", this.items); // Wird im Redirect über GET /list gesetzt!
//    //return "list"; // templates/list.html Redirect (Post-Get Redirect Pattern)
//    return "redirect:/list"; // templates/list.html Redirect (Post-Get Redirect Pattern)
//  }
    @GetMapping("delete")
    public String delete(Model model,
            @RequestParam String id) {
//    items = items.stream()
//            .filter(item -> !item.getTitle().equals(title))
//            .collect(Collectors.toList());

        // NIEMALS MIT FOR SCHLEIFE LÖSCHEN!!!!!
//    for (ToDoItem item : items) {
//      if (item.getTitle().equals(title)) {
//        items.remove(item);
//      }
//    }
        // NIEMALS MIT FOR SCHLEIFE LÖSCHEN!!!!!
        try {
            long idForItem = Long.parseLong(id);
            svc.delete(idForItem);
        } catch (NumberFormatException nfe) {
            System.err.println("Fehler beim Parsen der ID!");
        }
        return "redirect:/list";
    }

    @GetMapping("closed")
    public String doClosed(Model model,
            @RequestParam String id) {
        try {
            long idForItem = Long.parseLong(id);
            svc.setClosed(idForItem);
        } catch (NumberFormatException nfe) {
            System.err.println("Fehler beim Parsen der ID!");
        }
        return "redirect:/list";
    }

    private String generateCSVManual() {
        List<ToDoItem> items = svc.getTodoItems();
        final String NL = System.getProperty("line.separator");
        final String SEPARATOR = ";";
        StringBuilder sb = new StringBuilder("id;create;todo;title;text;closed" + NL);
        for (ToDoItem item : items) {
            sb.append(item.getId());
            sb.append(SEPARATOR);
            sb.append(item.getCreateDate());
            sb.append(SEPARATOR);
            sb.append(item.getTodoDate());
            sb.append(SEPARATOR);
            sb.append(item.getTitle());
            sb.append(SEPARATOR);
            sb.append(item.getText());
            sb.append(SEPARATOR);
            sb.append(item.isClosed() ? "Ja" : "Nein");
            sb.append(NL);
        }
        return sb.toString();
    }

    @GetMapping("/export")
    public ResponseEntity<String> doExport() {
        String exportText = generateCSVManual();

        return ResponseEntity
                .ok()
                .contentLength(exportText.length())
                .contentType(MediaType.TEXT_PLAIN)
                .body(exportText);
    }

    @GetMapping("/download")
    public ResponseEntity<String> doDownload() {
        String exportText = generateCSVManual();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=todo.csv");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(exportText.length())
                .body(exportText);
    }

    @GetMapping("/json")
    public ResponseEntity<Map<String, Object>> doJson() {
        Map<String, Object> result = new HashMap<>();
        result.put("vorname", "Thomas");
        result.put("nachname", "Knapp");
        result.put("code", 42);
        result.put("now", new Date());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

}
