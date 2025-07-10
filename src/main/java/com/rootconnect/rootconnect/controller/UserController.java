package com.rootconnect.rootconnect.controller;

import com.rootconnect.rootconnect.dto.UserMatchedResponse;
import com.rootconnect.rootconnect.dto.UserProfileUpdateRequest;
import com.rootconnect.rootconnect.model.Interest;
import com.rootconnect.rootconnect.model.User;
import com.rootconnect.rootconnect.model.UserInteraction;
import com.rootconnect.rootconnect.repository.InterestRepository;
import com.rootconnect.rootconnect.repository.UserInteractionRepository;
import com.rootconnect.rootconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final InterestRepository interestRepo;
    private final UserInteractionRepository interactionRepo;

    @GetMapping("/me")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ok(user);
    }

    @PostMapping("/interests")
    public ResponseEntity<String> updateInterests(@RequestBody List<String> interestNames, Authentication authentication) {
        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Interest> interests = interestNames.stream()
                .map(name -> interestRepo.findByName(name)
                        .orElseThrow(() -> new RuntimeException("Interest not found: " + name)))
                .collect(Collectors.toSet());

        user.setInterests(interests);
        userRepo.save(user);

        return ok("Interests updated successfully");
    }

    @PatchMapping("/me")
    public ResponseEntity<String> updateProfile(@RequestBody UserProfileUpdateRequest request, Authentication authentication) {
        User user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }
        if (request.getPersonalityType() != null) {
            user.setPersonalityType(request.getPersonalityType());
        }

        userRepo.save(user);
        return ok("Profile updated successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchUsers(@RequestParam(required = false) String location, Authentication authentication) {
        String currentEmail = authentication.getName();
        List<User> users = (location != null)
                ? userRepo.findByLocationContainingIgnoreCase(location)
                : userRepo.findAll();

        List<Map<String, Object>> result = users.stream()
                .filter(u -> !u.getEmail().equals(currentEmail))
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("username", u.getUsername());
                    map.put("location", u.getLocation());
                    return map;
                })
                .toList();

        return ok(result);
    }

    @GetMapping("/match")
    public ResponseEntity<?> matchUsers(@RequestParam(required = false) String location, Authentication authentication) {
        String currentEmail = authentication.getName();
        User currentUser = userRepo.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> allUsers = (location != null)
                ? userRepo.findByLocationContainingIgnoreCase(location)
                : userRepo.findAll();

        List<String> currentInterests = currentUser.getInterests().stream()
                .map(i -> i.getName()).toList();

        List<Map<String, Object>> strongMatches = allUsers.stream()
                .filter(u -> !u.getEmail().equals(currentEmail))
                .map(u -> {
                    List<String> otherInterests = u.getInterests().stream()
                            .map(i -> i.getName()).toList();

                    List<String> sharedInterests = currentInterests.stream()
                            .filter(otherInterests::contains)
                            .toList();

                    if (sharedInterests.isEmpty()) {
                        return null;
                    }

                    return Map.of(
                            "id", u.getId(),
                            "username", u.getUsername(),
                            "location", u.getLocation(),
                            "sharedInterests", sharedInterests,
                            "matchScore", sharedInterests.size()
                    );
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(m -> -(int) m.get("matchScore")))
                .toList();

        if(strongMatches.isEmpty()){
            List<Map<String, Object>> fallbackMatches = allUsers.stream()
                    .filter(u -> !u.getEmail().equals(currentEmail))
                    .filter(u -> u.getLocation()!=null && u.getLocation().equalsIgnoreCase(currentUser.getLocation()))
                    .map(u -> Map.of("id", u.getId(),
                            "username", u.getUsername(),
                            "location", u.getLocation(),
                            "sharedInterests", Collections.emptyList(),
                            "matchScore", 0))
                    .toList();

            return ResponseEntity.ok(fallbackMatches);
        }

        return ok(strongMatches);
    }

    @GetMapping("/interests")
    public ResponseEntity<List<String>> getAllInterests(){
        List<String> interests = interestRepo.findAll().stream()
                .map(Interest::getName)
                .sorted()
                .toList();
        return ok(interests);
    }

    @PostMapping("/{targetUserId}/like")
    public ResponseEntity<?> likeUser(@PathVariable Long targetUserId, Authentication authentication){
        return handleInteraction(targetUserId, authentication, UserInteraction.InteractionType.LIKE);
    }

    @PostMapping("/{targetUserId}/pass")
    public ResponseEntity<?> passUser(@PathVariable Long targetUserId, Authentication authentication) {
        return handleInteraction(targetUserId, authentication, UserInteraction.InteractionType.PASS);
    }

    private ResponseEntity<?> handleInteraction(Long targetUserId, Authentication authentication, UserInteraction.InteractionType type) {
        User sourceUser = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        User targetUser = userRepo.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        if (interactionRepo.existsBySourceUserAndTargetUser(sourceUser, targetUser)) {
            return ResponseEntity.badRequest().body("Already interacted with this user.");
        }

        UserInteraction interaction = UserInteraction.builder()
                .sourceUser(sourceUser)
                .targetUser(targetUser)
                .type(type)
                .build();

        interactionRepo.save(interaction);

        return ResponseEntity.ok(type + " recorded.");
    }
}
