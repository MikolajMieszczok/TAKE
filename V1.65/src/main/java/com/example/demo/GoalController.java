package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/goals")
public class GoalController {

    @Autowired private GoalService goalService;

    @GetMapping
    public List<GoalDTO> getAllGoals() {
        return goalService.getAllGoals().stream()
                .map(this::addLinks)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public GoalDTO getGoalById(@PathVariable Long id) {
        return addLinks(goalService.getGoalById(id));
    }

    @PostMapping
    public GoalDTO createGoal(@RequestBody GoalDTO dto) {
        return addLinks(goalService.createGoal(dto));
    }

    @PutMapping("/{id}")
    public GoalDTO updateGoal(@PathVariable Long id, @RequestBody GoalDTO dto) {
        return addLinks(goalService.updateGoal(id, dto));
    }

    @DeleteMapping("/{id}")
    public void deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
    }

    private GoalDTO addLinks(GoalDTO goal) {
        goal.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(GoalController.class).getGoalById(goal.getId())).withSelfRel());
        goal.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(GoalController.class).getAllGoals()).withRel("all-goals"));
        return goal;
    }
}
