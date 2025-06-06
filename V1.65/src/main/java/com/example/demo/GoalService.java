package com.example.demo;

import java.util.List;

public interface GoalService {
    List<GoalDTO> getAllGoals();
    GoalDTO getGoalById(Long id);
    GoalDTO createGoal(GoalDTO dto);
    GoalDTO updateGoal(Long id, GoalDTO dto);
    void deleteGoal(Long id);
}
