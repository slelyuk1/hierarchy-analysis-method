package com.leliuk.controller.control.custom.table;

import javafx.util.converter.NumberStringConverter;

class NonExceptionalNumberStringConverter extends NumberStringConverter {
    @Override
    public Number fromString(String value) {
        try {
            return super.fromString(value);
        } catch (RuntimeException e) {
            return null;
        }
    }
}
