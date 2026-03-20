package OtterStudio.backend_api.domain.auth.controller;

import OtterStudio.backend_api.domain.auth.dto.AuthResponse;
import OtterStudio.backend_api.domain.auth.dto.OAuthLoginRequest;
import OtterStudio.backend_api.domain.auth.dto.TokenRefreshRequest;
import OtterStudio.backend_api.domain.auth.dto.UserInfoResponse;
import OtterStudio.backend_api.domain.auth.service.AuthService;
import OtterStudio.backend_api.domain.user.entity.SocialProvider;
import OtterStudio.backend_api.global.common.response.ApiResponse;
import OtterStudio.backend_api.global.security.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/oauth/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(@Valid @RequestBody OAuthLoginRequest request) {
        AuthResponse response = authService.socialLogin(SocialProvider.GOOGLE, request.getCode());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/oauth/kakao")
    public ResponseEntity<ApiResponse<AuthResponse>> kakaoLogin(@Valid @RequestBody OAuthLoginRequest request) {
        AuthResponse response = authService.socialLogin(SocialProvider.KAKAO, request.getCode());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        AuthResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getMyInfo(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        Long userId = jwtTokenProvider.getUserId(token);
        UserInfoResponse response = authService.getMyInfo(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        Long userId = jwtTokenProvider.getUserId(token);
        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
