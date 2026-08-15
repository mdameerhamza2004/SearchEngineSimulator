package com.searchengine.simulator.model.dto.auth;

import com.searchengine.simulator.model.User;

public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String provider;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.provider = user.getProvider().name();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getProvider() { return provider; }
}
