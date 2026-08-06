package com.algaworks.algashop.authorizationserver.application.user.management;

import com.algaworks.algashop.authorizationserver.application.user.query.AuthUserOutput;
import com.algaworks.algashop.authorizationserver.domain.model.user.AuthUser;
import com.algaworks.algashop.authorizationserver.domain.model.user.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthUserManagementApplicationService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthUserOutput create(AuthUserInput input) {
        if (authUserRepository.existsByEmail(input.getEmail())) {
            throw new AuthUserEmailAlreadyInUseException(input.getEmail());
        }

        String tempPassword = RandomStringUtils.secure().randomAlphanumeric(12);

        System.out.println(tempPassword); //todo: send via email

        String passwordHash = passwordEncoder.encode(tempPassword);

        AuthUser authUser = AuthUser.brandNew(
                input.getName(),
                input.getEmail(),
                input.getType(),
                passwordHash
        );

        return AuthUserOutput.from(authUserRepository.save(authUser));
    }
}
