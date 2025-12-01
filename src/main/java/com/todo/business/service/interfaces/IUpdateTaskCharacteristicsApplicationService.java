package com.todo.business.service.interfaces;

import com.todo.business.model.implementation.PatchElement;
import com.todo.business.model.interfaces.ITaskDTO;

public interface IUpdateTaskCharacteristicsApplicationService {
    ITaskDTO execute(PatchElement patchElement);
}
