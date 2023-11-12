package com.service.inspection.mapper;

import com.service.inspection.dto.account.UserUpdate;
import com.service.inspection.dto.auth.UserSignUpDto;
import com.service.inspection.entities.Role;
import com.service.inspection.entities.User;
import com.service.inspection.service.UserDetailsImpl;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring"
)
public abstract class UserMapper {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Mapping(target = "password", source = "password", qualifiedByName = "createCryptPassword")
    @Mapping(target = "roles", source = "email", qualifiedByName = "userRole") // email just for mapping
    public abstract User mapToUser(UserSignUpDto userSignUpDto);

    @IterableMapping
    SimpleGrantedAuthority mapToGrantedAuthority(Role r) {
        return new SimpleGrantedAuthority(r.getName().name());
    }

    @Named("createCryptPassword")
    public String mapToCryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Named("userRole")
    List<Role> mapToNewUserRole(String dontNeed) {
        Role r = new Role();
        r.setId(1L);                 // для того, чтобы лишний раз не обращаться к бд
        return List.of(r);
    }

    @Mapping(target = "authorities", source = "roles")
    public abstract UserDetailsImpl mapToUserDetailsImpl(User user);

    @AfterMapping
    void map(@MappingTarget UserDetailsImpl userDetails, User user) {
        userDetails.setUser(user);
    }

    @Mapping(target = "password", ignore = true)
    public abstract void mapToUpdateUser(@MappingTarget User targetToUpdate, UserUpdate source);
}