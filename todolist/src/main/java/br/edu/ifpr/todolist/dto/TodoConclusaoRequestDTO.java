package br.edu.ifpr.todolist.dto;

import java.time.LocalDateTime;

// Entrada do PATCH /api/todos/{id}/conclusao: recebe apenas a data de conclusão.
public record TodoConclusaoRequestDTO(LocalDateTime finishedAt) {
}
