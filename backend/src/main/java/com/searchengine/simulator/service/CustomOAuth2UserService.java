package com.searchengine.simulator.service;

import com.searchengine.simulator.model.Provider;
import com.searchengine.simulator.model.User;
import com.searchengine.simulator.repository.UserRepository;
import com.searchengine.simulator.security.CustomUserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub"); // Google specific usually, or generic id

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (user.getProvider() != Provider.GOOGLE) {
                // If they originally signed up with local, and now try Google
                throw new OAuth2AuthenticationException("You're signed up with " + user.getProvider() + " account.");
            }
            user.setLastLogin(LocalDateTime.now());
            user = userRepository.save(user);
        } else {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setProvider(Provider.GOOGLE);
            user.setProviderId(providerId);
            user.setLastLogin(LocalDateTime.now());
            user = userRepository.save(user);
        }

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }
}
