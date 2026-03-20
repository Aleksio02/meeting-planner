package ru.epta.mtplanner.auth.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetListRequest {
    private String searchString;
}
