package OtterStudio.backend_api.global.security.oauth2;

import OtterStudio.backend_api.domain.user.entity.User;
import OtterStudio.backend_api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, attributes);

        saveOrUpdate(oAuthAttributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                getUserNameAttributeKey(registrationId)
        );
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository
                .findByProviderAndSocialId(attributes.getProvider(), attributes.getSocialId())
                .orElseGet(attributes::toUser);

        return userRepository.save(user);
    }

    private String getUserNameAttributeKey(String registrationId) {
        return switch (registrationId) {
            case "kakao" -> "id";
            case "naver" -> "response";
            default -> "sub"; // google
        };
    }
}
