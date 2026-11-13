package br.edu.ifpr.todolist.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpr.todolist.dto.TodoConclusaoRequestDTO;
import br.edu.ifpr.todolist.dto.TodoDetalhadoResponseDTO;
import br.edu.ifpr.todolist.dto.TodoRequestDTO;
import br.edu.ifpr.todolist.dto.TodoResponseDTO;
import br.edu.ifpr.todolist.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/todos")
@Tag(name = "Tarefas", description = "Cadastro, conclusão, listagem e exclusão de tarefas")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @Operation(summary = "Inserir tarefa", description = "Cadastra uma tarefa. id e createdAt são definidos pelo sistema.")
    @ApiResponse(responseCode = "201", description = "Tarefa criada")
    @PostMapping
    public ResponseEntity<TodoResponseDTO> criar(@RequestBody TodoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(
        summary = "Listar tarefas com filtro por status",
        description = "Sem o parâmetro status, retorna todas as tarefas. Use status=pendente ou status=concluida para filtrar."
    )
    @ApiResponse(responseCode = "200", description = "Lista de tarefas")
    @ApiResponse(responseCode = "400", description = "Status inválido. Use pendente ou concluida.")
    @GetMapping
    public ResponseEntity<List<TodoResponseDTO>> listar(
            @Parameter(description = "Status opcional: pendente ou concluida", example = "pendente")
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(service.listar(status));
    }

    @Operation(summary = "Excluir tarefa")
    @ApiResponse(responseCode = "204", description = "Tarefa excluída (sem corpo)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@Parameter(description = "ID da tarefa") @PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Informar conclusão", description = "Altera somente o finishedAt da tarefa e devolve a tarefa completa.")
    @ApiResponse(responseCode = "200", description = "Conclusão registrada")
    @PatchMapping("/{id}/conclusao")
    public ResponseEntity<TodoDetalhadoResponseDTO> concluir(
            @Parameter(description = "ID da tarefa") @PathVariable Long id,
            @RequestBody TodoConclusaoRequestDTO dto) {
        return ResponseEntity.ok(service.concluir(id, dto));
    }

    @Operation(summary = "Listar concluídas", description = "Tarefas com finishedAt preenchido.")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas concluídas (pode ser vazia)")
    @GetMapping("/concluidas")
    public ResponseEntity<List<TodoDetalhadoResponseDTO>> listarConcluidas() {
        return ResponseEntity.ok(service.listarConcluidas());
    }

    @Operation(summary = "Listar pendentes", description = "Tarefas com finishedAt nulo.")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas pendentes (pode ser vazia)")
    @GetMapping("/pendentes")
    
    public ResponseEntity<List<TodoDetalhadoResponseDTO>> listarPendentes() {
        return ResponseEntity.ok(service.listarPendentes());
    }
}
