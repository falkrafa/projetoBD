package bdproject.bdproject;

public class CustomUser {
    private boolean isLoggedIn;
    private boolean funcLogged;

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean LoggedIn) {
        this.isLoggedIn = LoggedIn;
    }

    public boolean isFuncLogged() {
        return funcLogged;
    }

    public void setFuncLogged(boolean funcLogged) {
        this.funcLogged = funcLogged;
    }
}
