package org.netuno.tritao.auth.providers;

public enum Callback {
    LOGIN("login"),
    REGISTER("register");

    private String key;

    Callback(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
