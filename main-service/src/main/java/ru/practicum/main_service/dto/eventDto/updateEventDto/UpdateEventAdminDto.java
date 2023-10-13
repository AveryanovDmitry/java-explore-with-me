package ru.practicum.main_service.dto.eventDto.updateEventDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main_service.model.event.StateActionForAdmin;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateEventAdminDto extends UpdateEventDto{
    private StateActionForAdmin stateAction;
}

