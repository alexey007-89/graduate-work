package ru.skypro.homework.service.impl;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.ResponseWrapperUser;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseWrapperUser getUsers() {
        List<User> userList = userRepository.findAll();
        List<UserDto> userDtoList = mapper.userToUserDto(userList);
        ResponseWrapperUser responseWrapperUser = new ResponseWrapperUser();
        if (!userDtoList.isEmpty()) {
            responseWrapperUser.setCount(userDtoList.size());
            responseWrapperUser.setResults(userDtoList);
        }
        return responseWrapperUser;
    }

    @Override
    public UserDto updateUser(UserDto userDto, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        if (user == null) {
            throw new NoSuchElementException();
        }
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        userRepository.save(user);
        return userDto;
    }

    @Override
    public UserDto getUser(int id) {
        User user = userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return mapper.userToUserDto(user);
    }
}
