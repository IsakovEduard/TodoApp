package com.todo.business.model.implementation;

import java.util.List;

public record PatchElement(String taskId, List<Characteristic> changeCharacteristics) {
}
