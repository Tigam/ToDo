package at.wifi.swdev.web.todo.service;

import at.wifi.swdev.web.todo.model.ToDoItem;
import at.wifi.swdev.web.todo.repository.TodoRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

@Service
@SessionScope
public class TodoService {

  @Autowired
  private TodoRepository repo;

  public List<ToDoItem> getTodoItems() {
    return repo.findAll();
  }

  public ToDoItem addTodoItem(ToDoItem item) {
    item.setCreateDate(new Date());
    return repo.save(item);
  }

  public ToDoItem editTodoItem(ToDoItem item) {
    return repo.save(item);
  }

  public void delete(ToDoItem item) {
    repo.delete(item);
  }

  public void delete(Long id) {
    repo.deleteById(id);
  }

  public Optional<ToDoItem> getToDoItem(Long id) {
    return repo.findById(id);
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
