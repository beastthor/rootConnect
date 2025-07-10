package com.rootconnect.rootconnect.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
