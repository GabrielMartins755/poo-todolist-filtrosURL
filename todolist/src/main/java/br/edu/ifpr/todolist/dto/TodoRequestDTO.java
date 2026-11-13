package br.edu.ifpr.todolist.dto;

import java.time.LocalDateTime;

// Entrada do POST /api/todos: o cliente só informa título e prazo.
// id, createdAt e finishedAt são definidos pelo sistema.
public record TodoRequestDTO(String title, LocalDateTime deadLine) {
}
