package Client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

@Component
public class UserServiceClient {
    private final RestClient restClient;

    public UserServiceClient(RestClient.Builder builder) {
        this.restClient = builder
        .baseUrl("http://" + System.getenv("TASK_MANAGER_SERVICE_NAME"))
        .build();
    }

     public Long createProfile(String username, String email) {
        ProfileResponse response = restClient.post()
            .uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new CreateProfileRequest(username, email))
            .retrieve()
            .body(ProfileResponse.class);

        return response.id();
    }

    public record CreateProfileRequest(String username, String email) {}
    public record ProfileResponse(Long id, Long teamId, String username, String email) {}
}

