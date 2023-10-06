package ru.practicum.main_service.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main_service.model.request.RequestStatus;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestStatusUpdateDto {
    private List<Long> requestIds;
    private RequestStatus status;
}