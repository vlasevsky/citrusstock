package com.citrusmall.citrusstock.util.converter;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StringToLongListConverter implements Converter<String, List<Long>> {

    @Override
    public List<Long> convert(String source) {
        // Удаляем пробелы
        String trimmed = source.trim();
        // Если строка обрамлена квадратными скобками, удаляем их
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }
        // Если после удаления скобок строка пуста, возвращаем пустой список
        if (trimmed.isEmpty()) {
            return List.of();
        }
        // Разбиваем строку по запятой и преобразуем каждую часть в Long
        return Arrays.stream(trimmed.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }
}