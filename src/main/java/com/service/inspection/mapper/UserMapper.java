package com.service.inspection.mapper;

import com.service.inspection.dto.account.UserUpdate;
import com.service.inspection.dto.auth.UserSignUpDto;
import com.service.inspection.entities.Role;
import com.service.inspection.entities.User;
import com.service.inspection.service.security.UserDetailsImpl;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        componentModel = "spring",
        uses = {EntityFactory.class}
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

    @Mapping(target = "authorities", source = "roles")
    @Mapping(target = "user", source = "user")
    public abstract UserDetailsImpl mapToUserDetailsImpl(User user);

    @Mapping(target = "password", ignore = true)
    public abstract void mapToUpdateUser(@MappingTarget User targetToUpdate, UserUpdate source);
}
