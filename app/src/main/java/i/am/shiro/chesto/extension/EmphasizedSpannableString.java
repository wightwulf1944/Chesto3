package i.am.shiro.chesto.extension;

import android.text.SpannableString;
import android.text.style.StyleSpan;

import java.util.LinkedList;
import java.util.List;

import static android.graphics.Typeface.BOLD;

public final class EmphasizedSpannableString extends SpannableString {

    public EmphasizedSpannableString(CharSequence source, String emphasis) {
        super(source);
        for (Occurrence occurrence : listOccurrences(emphasis, toString())) {
            StyleSpan styleSpan = new StyleSpan(BOLD);
            setSpan(styleSpan, occurrence.startIndex, occurrence.endIndex, SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private static List<Occurrence> listOccurrences(String of, String in) {
        List<Occurrence> occurrences = new LinkedList<>();

        int scanIndex = in.indexOf(of);
        while (scanIndex != -1) {
            Occurrence occurrence = new Occurrence();
            occurrence.startIndex = scanIndex;
            occurrence.endIndex = scanIndex + of.length();
            occurrences.add(occurrence);
            scanIndex = in.indexOf(of, occurrence.endIndex);
        }

        return occurrences;
    }

    private static class Occurrence {
        private int startIndex;
        private int endIndex;
    }
}
