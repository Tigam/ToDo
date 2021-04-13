package at.wifi.swdev.web.todo.repository;

import at.wifi.swdev.web.todo.model.ToDoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<ToDoItem, Long>{
  
}
