package com.example.java17_06_11.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.collection.spi.PersistentBag;

import java.util.ArrayList;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Apartments")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String address;
    private int rooms;
    private double price;
    private int metters;

    @ManyToOne // Определяем связь многие к одному
    @JoinColumn(name = "user_id") // Это поле связывает Apartments с User по user_id
    private User user; // Связь с сущностью User
    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    private PersistentBag<Rent> rents;
    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    private PersistentBag<Image> images;

}
