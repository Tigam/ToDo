package at.wifi.swdev.web.todo.service;

import at.wifi.swdev.web.todo.model.ToDoItem;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

@Service
@SessionScope
public class MockupService {

    private static final Logger LOG = Logger.getLogger(MockupService.class.getName());

    private List<ToDoItem> items;
    private long sequence = 0;

    @Value("${application.service.mockup.maxItems:99}")
    private int maxItemsCount = 15;
    
    @PostConstruct
    public void init() {
        LOG.info("Fill items...");
        Calendar cal = Calendar.getInstance();
        items = new ArrayList<>();
        for (int i = 1; i <= maxItemsCount; i++) {
            ToDoItem item = new ToDoItem();
            item.setTitle("Demo Item " + i);
            item.setText("Lorem ipsum");
            //item.setTodoDate(new Date());
            cal.add(Calendar.DATE, 1);
            item.setTodoDate(cal.getTime());
            if (i % 3 == 0) {
                item.setClosed(true);
            }
            addTodoItem(item);
        }
    }

    public List<ToDoItem> getTodoItems() {
        return this.items;
    }

    public ToDoItem addTodoItem(ToDoItem item) {
        sequence++;
        item.setId(sequence);
        item.setCreateDate(new Date());
        items.add(item);
        return item;
    }

    public ToDoItem editTodoItem(ToDoItem item) {
        int position = items.indexOf(item);
        items.set(position, item);
        return item;
    }

    public void delete(ToDoItem item) {
        items.remove(item);
    }

    public void delete(Long id) {
        // Mit Streams...
//        List<ToDoItem> newItemsList = items.stream().filter(item -> item.getId().equals(id)).collect(Collectors.toList());
//        items.clear();
//        items.addAll(newItemsList);
        // easy Methode
        ToDoItem intItem = new ToDoItem();
        intItem.setId(id);
        items.remove(intItem);
    }

    public Optional<ToDoItem> getToDoItem(Long id) {
        return items.stream()
                //.parallel()
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    public void setClosed(Long id) {
        Optional<ToDoItem> itemOpt = getToDoItem(id);
        if (itemOpt.isPresent()) {
            ToDoItem item = itemOpt.get();
            item.setClosed(true);
            editTodoItem(item);
        }
    }
}
