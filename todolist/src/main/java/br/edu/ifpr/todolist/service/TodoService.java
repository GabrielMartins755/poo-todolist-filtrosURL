package br.edu.ifpr.todolist.service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import br.edu.ifpr.todolist.dto.TodoConclusaoRequestDTO;
import br.edu.ifpr.todolist.dto.TodoDetalhadoResponseDTO;
import br.edu.ifpr.todolist.dto.TodoRequestDTO;
import br.edu.ifpr.todolist.dto.TodoResponseDTO;
import br.edu.ifpr.todolist.model.Todo;
import br.edu.ifpr.todolist.repository.TodoRepository;

@Service
public class TodoService {

    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    public TodoResponseDTO criar(TodoRequestDTO dto) {
        Todo todo = new Todo();
        todo.setTitle(dto.title());
        todo.setDeadLine(dto.deadLine());
        todo.setCreatedAt(LocalDateTime.now()); // regra: sistema define a criação
        todo.setFinishedAt(null);               // regra: começa sem conclusão
        return TodoResponseDTO.fromEntity(repository.save(todo));
    }

    public List<TodoResponseDTO> listar(String status) {
        // Sem filtro, retorna todas as tarefas.
        if (status == null || status.isBlank()) {
            return repository.findAll().stream()
                    .map(TodoResponseDTO::fromEntity)
                    .toList();
        }

        // Padroniza o texto para aceitar, por exemplo, PENDENTE ou Pendente.
        String statusNormalizado = status.trim().toLowerCase(Locale.ROOT);

        return switch (statusNormalizado) {
            case "pendente" -> repository.findByFinishedAtIsNull().stream()
                    .map(TodoResponseDTO::fromEntity)
                    .toList();
            case "concluida" -> repository.findByFinishedAtIsNotNull().stream()
                    .map(TodoResponseDTO::fromEntity)
                    .toList();
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status inválido. Use 'pendente' ou 'concluida'."
            );
        };
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    public TodoDetalhadoResponseDTO concluir(Long id, TodoConclusaoRequestDTO dto) {
        // Tratamento de ID inexistente será visto em aula específica.
        Todo todo = repository.findById(id).orElseThrow();
        todo.setFinishedAt(dto.finishedAt()); // altera SOMENTE finishedAt
        return TodoDetalhadoResponseDTO.fromEntity(repository.save(todo));
    }

    public List<TodoDetalhadoResponseDTO> listarConcluidas() {
        return repository.findByFinishedAtIsNotNull().stream().map(TodoDetalhadoResponseDTO::fromEntity).toList();
    }

    public List<TodoDetalhadoResponseDTO> listarPendentes() {
        return repository.findByFinishedAtIsNull().stream().map(TodoDetalhadoResponseDTO::fromEntity).toList();
    }
}
