package br.edu.ifpr.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(
		title = "API de Tarefas (todolist)",
		version = "1.0",
		description = "Projeto bimestral de POO — IFPR Cascavel. CRUD de tarefas com conclusão e listagens de concluídas/pendentes."))
@SpringBootApplication
public class TodolistApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodolistApplication.class, args);
	}

}
