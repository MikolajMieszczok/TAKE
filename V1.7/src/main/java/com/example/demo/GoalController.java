package com.example.demo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goals")
public class GoalController {
	
	@Autowired
    private GoalRepository GoalRepository;
	
//	@GetMapping
//    public List<Goal> getAllGoals() {
//        return GoalRepository.findAll();
//    }

	@GetMapping
	public @ResponseBody CollectionModel<GoalDTO> getCars() {
	List<GoalDTO> goalsDTO = new ArrayList<>();
	for(Goal goal:GoalRepository.findAll())
	goalsDTO.add(new GoalDTO(goal));
	return CollectionModel.of(goalsDTO);
	}
	
//    @GetMapping("/{id}")
//    public ResponseEntity<Goal> getById(@PathVariable("id") Long id) {
//        Optional<Goal> goal = GoalRepository.findById(id);
//        return goal.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }
	
	
	@GetMapping("/{id}")
	public ResponseEntity<GoalDTO> getById(@PathVariable("id") Long id){
		Optional<Goal> goal = GoalRepository.findById(id);
		
		if(goal.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		GoalDTO goalDTO = new GoalDTO(goal.get());
		
		return ResponseEntity.ok(goalDTO);
		
	}
	
	
    @PostMapping
    public Goal createGoal(@RequestBody Goal goal) {
    return GoalRepository.save(goal);
    }
    @DeleteMapping
    public ResponseEntity<Void> deleteAllGoals() {
        GoalRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
