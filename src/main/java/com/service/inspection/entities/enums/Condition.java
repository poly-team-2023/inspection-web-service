package com.service.inspection.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Condition {
    EMERGENCY("Аварийное"), LIMITED_OPERABLE("Ограничено работоспособное"),
    OPERABLE("Работоспособное"), SATISFACTORY("Удовлетворительный"),
    SERVICEABLE("Исправное"), UNACCEPTABLE("Удовлетворительное"),
    UNSATISFACTORY("Неудовлетворительное");

    private final String name;
}
