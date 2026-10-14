package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public final class ItemMapper {

    private ItemMapper() {
    }

    public static ItemDto toDto(Item model) {
        ItemDto dto = new ItemDto();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setAvailable(model.getAvailable());
        return dto;
    }

    public static Item toModel(ItemDto dto) {
        Item model = new Item();
        model.setId(dto.getId());
        model.setName(dto.getName());
        model.setDescription(dto.getDescription());
        model.setAvailable(dto.getAvailable());
        return model;
    }
}
