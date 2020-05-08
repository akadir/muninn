package io.github.akadir.muninn.helper;

import com.github.difflib.algorithm.DiffException;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import static io.github.akadir.muninn.helper.Constants.NEW_TAG;
import static io.github.akadir.muninn.helper.Constants.OLD_TAG;

/**
 * @author akadir
 * Date: 8.05.2020
 * Time: 22:59
 */
public class DiffHelper {
    private static final Logger logger = LoggerFactory.getLogger(DiffHelper.class);

    private DiffHelper() {
    }

    public static String generateDiff(String oldData, String newData) {
        DiffRowGenerator generator = DiffRowGenerator
                .create()
                .showInlineDiffs(true)
                .mergeOriginalRevised(true)
                .inlineDiffByWord(true)
                .oldTag(f -> Boolean.TRUE.equals(f) ? "<strike>" : "</strike>")
                .newTag(f -> Boolean.TRUE.equals(f) ? "<b>" : "</b>")
                .build();


        try {
            List<DiffRow> rows = generator.generateDiffRows(
                    Collections.singletonList(oldData),
                    Collections.singletonList(newData));
            StringBuilder diff = new StringBuilder(rows.get(0).getOldLine());

            for (int i = 1; i < rows.size(); i++) {
                diff.append("\n").append(rows.get(i).getOldLine());
            }
            return diff.toString();
        } catch (DiffException e) {
            logger.error("Error while generating diff: ", e);
        }

        return OLD_TAG + oldData + "\n" + NEW_TAG + newData;
    }
}
