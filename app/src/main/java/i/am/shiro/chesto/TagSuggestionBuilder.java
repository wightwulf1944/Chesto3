package i.am.shiro.chesto;

import i.am.shiro.chesto.extension.EmphasizedSpannableString;
import i.am.shiro.chesto.model.Tag;
import i.am.shiro.chesto.model.TagSuggestion;

public class TagSuggestionBuilder {

    private String emphasis;

    public TagSuggestionBuilder(String emphasis) {
        this.emphasis = emphasis;
    }

    public TagSuggestion makeFrom(Tag tag) {
        String postCount = postCountFrom(tag.getPostCount());
        CharSequence name = nameFrom(tag.getName());
        return new TagSuggestion(postCount, name);
    }

    private String postCountFrom(int postCount) {
        String postCountStr = String.valueOf(postCount);
        if (postCount < 1_000) {
            return postCountStr;
        } else if (postCount < 10_000) {
            return new StringBuilder(postCountStr)
                    .reverse()
                    .delete(0, 2)
                    .reverse()
                    .insert(1, '.')
                    .append('k')
                    .toString();
        } else if (postCount < 1_000_000) {
            return new StringBuilder(postCountStr)
                    .reverse()
                    .delete(0, 3)
                    .reverse()
                    .append('k')
                    .toString();
        } else if (postCount < 10_000_000) {
            return new StringBuilder(postCountStr)
                    .reverse()
                    .delete(0, 5)
                    .reverse()
                    .insert(1, '.')
                    .append('m')
                    .toString();
        } else {
            return new StringBuilder(postCountStr)
                    .reverse()
                    .delete(0, 6)
                    .reverse()
                    .append('m')
                    .toString();
        }
    }

    private CharSequence nameFrom(String text) {
        if (emphasis.isEmpty()) return text;

        return new EmphasizedSpannableString(text, emphasis);
    }
}
