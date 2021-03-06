package io.github.shriohoo.ecommerce.user.adapter.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.github.shriohoo.ecommerce.user.domain.User;
import io.github.shriohoo.ecommerce.user.service.UserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<ResponseUser>> findAll() {
        return ResponseEntity.ok(
            userService.findAll()
                .stream()
                .map(ResponseUser::convert)
                .collect(Collectors.toUnmodifiableList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseUser> find(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseUser.convert(userService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ResponseUser> create(@RequestBody RequestUser requestUser) {
        User user = userService.save(requestUser.toUser());
        ResponseUser responseUser = ResponseUser.convert(user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(responseUser);
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> delete(@RequestBody RequestUser requestUser) {
        userService.delete(requestUser.toUser());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Value(staticConstructor = "of")
    public static class RequestUser {

        @Email(message = "Email format is incorrect")
        @NotNull(message = "Email cannot be null")
        @Size(min = 2, message = "Email not be less than two characters")
        String email;

        @NotNull(message = "Username cannot be null")
        @Size(min = 2, message = "Email not be less than two characters")
        String username;

        @NotNull(message = "Password cannot be null")
        @Size(min = 8, max = 16, message = "Password must be equal or grater than 8 characters and less than 16 characters")
        String password;

        public User toUser() {
            return User.builder()
                .email(email)
                .username(username)
                .password(password)
                .build();
        }

    }

    @JsonInclude(Include.NON_NULL)
    @Value(staticConstructor = "of")
    public static class ResponseUser {

        String email;
        String username;
        LocalDateTime createdAt;
        List<ResponseOrder> orders;

        @Builder
        private ResponseUser(String email, String username, LocalDateTime createdAt, List<ResponseOrder> orders) {
            this.email = email;
            this.username = username;
            this.createdAt = createdAt;
            this.orders = orders;
        }

        public static ResponseUser convert(User user) {
            return ResponseUser.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .orders(new ArrayList<>())
                .build();
        }

    }

    @JsonInclude(Include.NON_NULL)
    @Value(staticConstructor = "of")
    public static class ResponseOrder {

        String productId;
        Integer quantity;
        Integer unitPrice;
        Integer totalPrice;
        LocalDateTime createdAt;
        String orderId;

    }

}
