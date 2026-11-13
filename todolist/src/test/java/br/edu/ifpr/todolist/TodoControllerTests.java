package br.edu.ifpr.todolist;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Reproduz o roteiro de testes do Thunder Client.
@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTests {

    @Autowired
    MockMvc mvc;

    @Test
    void roteiroCompleto() throws Exception {
        // 1. Cadastrar duas tarefas
        mvc.perform(post("/api/todos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Estudar Spring Boot\",\"deadLine\":\"2026-10-30T18:00:00\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.finishedAt").doesNotExist());
        mvc.perform(post("/api/todos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Fazer trabalho de POO\",\"deadLine\":\"2026-11-10T23:59:00\"}"))
                .andExpect(status().isCreated());

        // 2 e 4. Informar conclusão e conferir a data na resposta
        mvc.perform(patch("/api/todos/1/conclusao").contentType(MediaType.APPLICATION_JSON)
                .content("{\"finishedAt\":\"2026-10-22T10:30:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Estudar Spring Boot"))
                .andExpect(jsonPath("$.finishedAt").value("2026-10-22T10:30:00"));

        // 3. Verificar separação
        mvc.perform(get("/api/todos/concluidas")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].id").value(1));
        mvc.perform(get("/api/todos/pendentes")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].id").value(2));

        // 5. Excluir e listar de novo
        mvc.perform(delete("/api/todos/1")).andExpect(status().isNoContent())
                .andExpect(content().string(""));
        mvc.perform(get("/api/todos")).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2));
        mvc.perform(get("/api/todos/concluidas")).andExpect(content().json("[]"));
    }

    @Test
    void swaggerDocumentaOsEndpoints() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("API de Tarefas (todolist)"))
                .andExpect(jsonPath("$.paths['/api/todos']").exists())
                .andExpect(jsonPath("$.paths['/api/todos/{id}/conclusao']").exists());
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }
}
