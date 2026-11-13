package br.edu.ifpr.todolist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.todolist.model.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // SELECT ... WHERE finished_at IS NOT NULL
    List<Todo> findByFinishedAtIsNotNull();

    // SELECT ... WHERE finished_at IS NULL
    List<Todo> findByFinishedAtIsNull();
}
