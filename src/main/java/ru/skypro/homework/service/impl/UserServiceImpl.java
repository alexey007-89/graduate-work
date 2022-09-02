package ru.skypro.homework.service.impl;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.ResponseWrapperUser;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get user by username
     *
     * @param username - username from client
     * @return found user as responseWrapperUser (DTo)
     */
    @Override
    public ResponseWrapperUser getCurrentUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException();
        }
        UserDto userDto = mapper.userToUserDto(user);
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(userDto);
        ResponseWrapperUser responseWrapperUser = new ResponseWrapperUser();
        responseWrapperUser.setCount(userDtoList.size());
        responseWrapperUser.setResults(userDtoList);
        return responseWrapperUser;
    }

    /**
     * Update user
     *
     * @param userDto   - user information from client
     * @param principal - user principal
     * @return updated user as UserDto (DTO)
     */
    @Override
    public UserDto updateUser(UserDto userDto, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        if (user == null) {
            throw new UserNotFoundException();
        }
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhone(userDto.getPhone());
        userRepository.save(user);
        return userDto;
    }

    /**
     * Get user by Id
     *
     * @param id - user's id
     * @return user as UserDto (DTO)
     */
    @Override
    public UserDto getUser(int id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return mapper.userToUserDto(user);
    }
}
