package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Pagination;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final UserMapper userMapper;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = userMapper.userFromDto(userService.findById(userId));
        ItemRequest itemRequest = itemRequestMapper.itemRequestFromDto(itemRequestDto);
        itemRequest.setRequestor(requestor);
        return itemRequestMapper.itemRequestToDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllRequestByUser(Long userId) {
        userService.findById(userId);
        List<ItemRequestDto> itemRequestDtoList = itemRequestRepository.findByRequestorIdOrderByCreatedAsc(userId).stream()
                .map(itemRequestMapper::itemRequestToDto)
                .collect(Collectors.toList());

        return itemRequestDtoList;
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userService.findById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(ItemRequest.class, String.format("ID: %s", requestId)));

        return itemRequestMapper.itemRequestToDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        userService.findById(userId);
        Pagination page = new Pagination(from, size);

        return itemRequestRepository.findAllExceptRequestorIdOrderByCreatedAsc(userId, page)
                .stream()
                .map(itemRequestMapper::itemRequestToDto)
                .collect(Collectors.toList());
    }
}
