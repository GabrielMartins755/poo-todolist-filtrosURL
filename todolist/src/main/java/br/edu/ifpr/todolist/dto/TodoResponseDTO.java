package br.edu.ifpr.todolist.dto;

import java.time.LocalDateTime;

import br.edu.ifpr.todolist.model.Todo;

// DTO de resposta "atual": NÃO possui finishedAt (mantido como estava).
public record TodoResponseDTO(Long id, String title, LocalDateTime createdAt, LocalDateTime deadLine) {

    public static TodoResponseDTO fromEntity(Todo todo) {
        return new TodoResponseDTO(todo.getId(), todo.getTitle(), todo.getCreatedAt(), todo.getDeadLine());
    }
}
