package fr.flowsqy.stelyclaimconfig.menu.item.flag.effect.state.text;

public class TextStateEffect {

    private final String allow;
    private final String deny;

    public TextStateEffect(String allow, String deny) {
        this.allow = allow;
        this.deny = deny;
    }

    public String get(boolean state) {
        return state ? allow : deny;
    }

    public String getAllow() {
        return allow;
    }

    public String getDeny() {
        return deny;
    }

}
