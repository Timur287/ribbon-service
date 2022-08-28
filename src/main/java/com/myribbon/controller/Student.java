package com.myribbon.controller;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student {

    String firstname;

    String lastname;

    int age;

    int course;

    double avGrade;

    boolean dormitoryRequired;
}
