package i.am.shiro.chesto.model;

public class TagSuggestion {

    private final String postCount;

    private final CharSequence name;

    public TagSuggestion(String postCount, CharSequence name) {
        this.postCount = postCount;
        this.name = name;
    }

    public String getPostCount() {
        return postCount;
    }

    public CharSequence getName() {
        return name;
    }
}
