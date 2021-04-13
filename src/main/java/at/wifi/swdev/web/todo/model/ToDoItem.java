package at.wifi.swdev.web.todo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class ToDoItem implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull
  @Size(min = 3, max = 100, message = "Bitte geben Sie einen Titel mit mindestens drei Zeichen ein.")
  @Column(length = 100)
  private String title;

  @NotNull
  @Size(min = 3, max = 1024, message = "Bitte geben Sie einen Text mit mindestens drei Zeichen ein.")
  @Column(length = 1024)
  private String text;

  @DateTimeFormat(pattern = "dd.MM.yyyy")
  private Date createDate;
  

  @DateTimeFormat(pattern = "dd.MM.yyyy")
  private Date todoDate;  
  
  private boolean closed = false;
  
  public ToDoItem() {
   
  }

  public ToDoItem(String title, String text) {
    this();
    this.title = title;
    this.text = text;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

 
  @Override
  public int hashCode() {
    int hash = 5;
    hash = 47 * hash + Objects.hashCode(this.id);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ToDoItem other = (ToDoItem) obj;
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public boolean isClosed() {
    return closed;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  @Override
  public String toString() {
    return "ToDoItem{" + "id=" + id + ", title=" + title + ", text=" + text + ", createDate=" + createDate + ", todoDate=" + todoDate + ", closed=" + closed + '}';
  }


  public Date getTodoDate() {
    return todoDate;
  }

  public void setTodoDate(Date todoDate) {
    this.todoDate = todoDate;
  }

 

}
