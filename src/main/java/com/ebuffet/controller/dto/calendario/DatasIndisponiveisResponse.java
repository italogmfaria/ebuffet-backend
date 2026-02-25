package com.ebuffet.controller.dto.calendario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DatasIndisponiveisResponse {

    private List<LocalDate> datas;
}
