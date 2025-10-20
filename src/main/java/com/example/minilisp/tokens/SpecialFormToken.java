package com.example.minilisp.tokens;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpecialFormToken extends Token {

    private final SpecialForm specialForm;

    @Override
    public String toString() {
        return specialForm.toString();
    }
}
