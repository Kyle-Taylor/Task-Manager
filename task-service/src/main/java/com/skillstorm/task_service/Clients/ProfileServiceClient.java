package com.skillstorm.task_service.Clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.skillstorm.task_service.Exceptions.InvalidReferenceException;

@Component
public class ProfileServiceClient {

    private final RestClient restClient;

    public ProfileServiceClient(
        RestClient.Builder restClientBuilder,
        @Value("${profile.service.base-url}") String profileServiceBaseUrl
    ) {
        this.restClient = restClientBuilder
            .baseUrl(profileServiceBaseUrl)
            .build();
    }

    public void validateUserExists(Long userId) {
        if (userId == null) {
            return;
        }

        ensureResourceExists("/api/profiles/users/" + userId, "User not found with id: " + userId);
    }

    public void validateTeamExists(Long teamId) {
        if (teamId == null) {
            return;
        }

        ensureResourceExists("/api/profiles/teams/" + teamId, "Team not found with id: " + teamId);
    }

    private void ensureResourceExists(String path, String notFoundMessage) {
        try {
            restClient.get()
                .uri(path)
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new InvalidReferenceException(notFoundMessage);
            }

            throw new IllegalStateException("Unable to validate profile-service reference", e);
        }
    }
}
