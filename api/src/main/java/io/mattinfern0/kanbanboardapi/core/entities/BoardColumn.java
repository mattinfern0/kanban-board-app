package io.mattinfern0.kanbanboardapi.core.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "board_column")
public class BoardColumn {
    @Id
    @GeneratedValue
    UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "board_id")
    Board board;

    @NotNull
    @Column(name = "title")
    String title;

    @NotNull
    @Min(0)
    @Column(name = "display_order")
    Integer displayOrder;

    @ManyToOne
    @JoinColumn(name = "task_status_id")
    @NotNull
    TaskStatus taskStatus;

    @OneToMany(mappedBy = "boardColumn")
    @OrderBy("boardColumnOrder")
    List<Task> tasks = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        if (this.board != null) {
            this.board.getBoardColumns().remove(this);
        }

        if (board != null && !board.getBoardColumns().contains(this)) {
            board.getBoardColumns().add(this);
        }

        this.board = board;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        int maxOrder = tasks.stream().mapToInt(Task::getBoardColumnOrder).max().orElse(0);

        // Set task to last in order
        insertTask(task, maxOrder + 1);
    }

    public void insertTask(Task task, int orderIndex) {
        task.setBoardColumn(this);
        task.setTaskStatus(this.taskStatus);

        task.setBoardColumnOrder(orderIndex);

        if (orderIndex < tasks.size()) {
            tasks.add(orderIndex, task);
        } else {
            tasks.add(task);
        }
    }

    public void removeTask(Task task) {
        task.setBoardColumn(null);
        task.setBoardColumnOrder(null);
        tasks.remove(task);
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
}
