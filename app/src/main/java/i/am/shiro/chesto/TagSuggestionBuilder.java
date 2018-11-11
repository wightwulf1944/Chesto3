package i.am.shiro.chesto;

import android.text.SpannableString;
import android.text.style.StyleSpan;

import i.am.shiro.chesto.model.Tag;
import i.am.shiro.chesto.model.TagSuggestion;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

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

        SpannableString formattedText = new SpannableString(text);

        int scanIndex = 0;
        do {
            StyleSpan boldStyleSpan = new StyleSpan(BOLD);
            int startIndex = text.indexOf(emphasis, scanIndex);
            int endIndex = startIndex + emphasis.length();
            formattedText.setSpan(boldStyleSpan, startIndex, endIndex, SPAN_EXCLUSIVE_EXCLUSIVE);
            scanIndex = endIndex;
        } while (text.substring(scanIndex).contains(emphasis));

        return formattedText;
    }
}
