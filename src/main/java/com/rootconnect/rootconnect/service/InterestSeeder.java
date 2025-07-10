package com.rootconnect.rootconnect.service;

import com.rootconnect.rootconnect.model.Interest;
import com.rootconnect.rootconnect.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InterestSeeder implements CommandLineRunner {

    private final InterestRepository interestRepo;
    @Override
    public void run(String... args) throws Exception {
        List<String> defaultInterests = List.of(
                "Technology",
                "Health & Wellness",
                "Travel",
                "Food & Cooking",
                "Sports & Fitness",
                "Arts & Culture",
                "Finance & Investing",
                "Education & Learning",
                "Environment & Sustainability",
                "Fashion & Beauty"
        );

        defaultInterests.forEach(name -> {
            if(interestRepo.findByName(name).isEmpty()) {
                interestRepo.save(Interest.builder().name(name).build());
            } else {
                System.out.println("Interest already exists: " + name);
            }
        });
        System.out.println("Default interests loaded.");
    }
}
