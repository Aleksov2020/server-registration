package com.app.chatserver.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "avatar")
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "size")
    private Long size;
    @Column(name="path")
    private String path;
}
