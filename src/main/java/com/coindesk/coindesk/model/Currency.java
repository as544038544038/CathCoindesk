package com.coindesk.coindesk.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@Table(name = "currency")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Currency {

    @Id
    private String code;

    @Column(name = "name_zh")
    private String nameZh;

    @Column(name = "rate")
    private Double rate;
}
