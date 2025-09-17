package com.todo.business.service.interfaces;

import com.todo.business.model.implementation.PatchElement;
import com.todo.business.model.interfaces.ITask;

public interface IUpdateTaskCharacteristicsApplicationService {
    ITask execute(String userId, PatchElement patchElement);
}
