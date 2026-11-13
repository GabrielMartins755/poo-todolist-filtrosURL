package br.edu.ifpr.todolist.dto;

import java.time.LocalDateTime;

import br.edu.ifpr.todolist.model.Todo;

// Resposta completa, com finishedAt, para o cliente conferir a data de conclusão.
// Usada no PATCH de conclusão e nas listagens de concluídas/pendentes.
public record TodoDetalhadoResponseDTO(Long id, String title, LocalDateTime createdAt,
        LocalDateTime deadLine, LocalDateTime finishedAt) {

    public static TodoDetalhadoResponseDTO fromEntity(Todo todo) {
        return new TodoDetalhadoResponseDTO(todo.getId(), todo.getTitle(), todo.getCreatedAt(),
                todo.getDeadLine(), todo.getFinishedAt());
    }
}
