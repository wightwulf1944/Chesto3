package i.am.shiro.chesto.model;

public class SearchSuggestion {

    private final int id;

    private final String postCount;

    private final CharSequence name;

    private boolean isSelected;

    private SearchSuggestion(Builder builder) {
        id = builder.id;
        postCount = builder.postCount;
        name = builder.name;
        isSelected = builder.isSelected;
    }

    public int getId() {
        return id;
    }

    public String getPostCount() {
        return postCount;
    }

    public CharSequence getName() {
        return name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public static class Builder {

        private int id;

        private String postCount;

        private CharSequence name;

        private boolean isSelected;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setPostCount(String postCount) {
            this.postCount = postCount;
            return this;
        }

        public Builder setName(CharSequence name) {
            this.name = name;
            return this;
        }

        public Builder setSelected(boolean selected) {
            this.isSelected = selected;
            return this;
        }

        public SearchSuggestion build() {
            return new SearchSuggestion(this);
        }
    }
}
