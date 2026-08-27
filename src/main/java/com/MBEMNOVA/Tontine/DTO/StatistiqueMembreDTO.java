package com.MBEMNOVA.Tontine.DTO;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatistiqueMembreDTO {

    private Long membreId;

    private String nomMembre;

    private BigDecimal totalCotise;

    private BigDecimal totalRecu;

    private BigDecimal soldeNet;
}