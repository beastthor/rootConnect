package com.rootconnect.rootconnect.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User sourceUser;

    @ManyToOne
    private User targetUser;

    @Enumerated(EnumType.STRING)
    private InteractionType type;

    public enum InteractionType {
        LIKE,
        PASS
    }
}
