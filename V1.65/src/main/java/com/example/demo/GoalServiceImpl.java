package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalServiceImpl implements GoalService {

    @Autowired private GoalRepository goalRepository;
    @Autowired private GoalMapper goalMapper;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private MatchRepository matchRepository;

    @Override
    public List<GoalDTO> getAllGoals() {
        return goalRepository.findAll().stream().map(goalMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public GoalDTO getGoalById(Long id) {
        return goalMapper.toDTO(goalRepository.findById(id).orElseThrow());
    }

    @Override
    public GoalDTO createGoal(GoalDTO dto) {
        Goal goal = goalMapper.toEntity(dto);
        goal.setPlayer(playerRepository.findById(dto.getPlayerId()).orElseThrow());
        goal.setMatch(matchRepository.findById(dto.getMatchId()).orElseThrow());
        return goalMapper.toDTO(goalRepository.save(goal));
    }

    @Override
    public GoalDTO updateGoal(Long id, GoalDTO dto) {
        Goal goal = goalRepository.findById(id).orElseThrow();
        goal.setOwnGoal(dto.getOwnGoal());
        goal.setPlayer(playerRepository.findById(dto.getPlayerId()).orElseThrow());
        goal.setMatch(matchRepository.findById(dto.getMatchId()).orElseThrow());
        return goalMapper.toDTO(goalRepository.save(goal));
    }

    @Override
    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }
}
