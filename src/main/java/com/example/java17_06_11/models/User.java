package com.example.java17_06_11.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.collection.spi.PersistentBag;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;
    private boolean active;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private PersistentBag<UserRole> roles;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private PersistentBag<Apartment> apartments;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private PersistentBag<Rent> rents;
    public static boolean isAdmin(User user)
    {
        if(user==null)
            return false;
        var roles = user.getRoles();
        if(roles==null)
            return false;
        return roles.stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()>0;
    }
}
