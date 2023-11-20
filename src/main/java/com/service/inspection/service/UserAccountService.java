package com.service.inspection.service;

import com.service.inspection.configs.BucketName;
import com.service.inspection.dto.account.PasswordDto;
import com.service.inspection.dto.account.UserUpdate;
import com.service.inspection.entities.User;
import com.service.inspection.mapper.UserMapper;
import com.service.inspection.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserAccountService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final StorageService storageService;
    private final PasswordEncoder passwordEncoder; // TODO вынести в отдельный сервис

    public void updateUser(User targetUpdate, UserUpdate sourceUpdate) {
        userMapper.mapToUpdateUser(targetUpdate, sourceUpdate);
        userRepository.save(targetUpdate);
    }

    @Transactional
    public void setUserLogo(User targetUser, MultipartFile multipartFile) {
        UUID logoUuid = UUID.randomUUID();

        targetUser.setLogoUuid(logoUuid);
        targetUser.setLogoName(multipartFile.getOriginalFilename());
        userRepository.save(targetUser);

        storageService.saveFile(BucketName.USER_LOGO, logoUuid.toString(), multipartFile);
    }

    public StorageService.BytesWithContentType getUserLogo(User user) {
        UUID logoUuid = user.getLogoUuid();
        if (logoUuid == null) return null;

        return storageService.getFile(BucketName.USER_LOGO, logoUuid.toString());
    }

    public boolean changeUserPassword(User user, PasswordDto passwordDto) {
        if (!passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())) {
            return false; // TODO обработка неправильно введенного пароля
        }
        user.setPassword(userMapper.mapToCryptPassword(passwordDto.getNewPassword()));
        userRepository.save(user);
        return true;
    }
}
