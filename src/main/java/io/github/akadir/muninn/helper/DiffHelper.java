package io.github.akadir.muninn.helper;

import com.github.difflib.algorithm.DiffException;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * @author akadir
 * Date: 8.05.2020
 * Time: 22:59
 */
public class DiffHelper {
    private static final Logger logger = LoggerFactory.getLogger(DiffHelper.class);
    private static final DiffRowGenerator generator;

    private static final Function<Boolean, String> DELETED_TAG = f -> Boolean.TRUE.equals(f) ? "<strike>" : "</strike>";
    private static final Function<Boolean, String> INSERTED_TAG = f -> Boolean.TRUE.equals(f) ? "<u><b>" : "</b></u>";

    static {
        generator = DiffRowGenerator
                .create()
                .showInlineDiffs(true)
                .mergeOriginalRevised(true)
                .inlineDiffByWord(true)
                .oldTag(DELETED_TAG)
                .newTag(INSERTED_TAG)
                .build();
    }

    private DiffHelper() {
    }

    public static String generateDiff(String oldData, String newData) {
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

        return Constants.OLD_TAG + oldData + "\n" + Constants.NEW_TAG + newData;
    }

    public static String generateDiffGoogle(String oldData, String newData) {
        StringBuilder diff = new StringBuilder();

        DiffMatchPatch dmp = new DiffMatchPatch();
        LinkedList<DiffMatchPatch.Diff> diffMain = dmp.diffMain(oldData, newData);
        dmp.diffCleanupSemantic(diffMain);

        for (DiffMatchPatch.Diff diffMatchPatch : diffMain) {
            if (diffMatchPatch.operation == DiffMatchPatch.Operation.EQUAL) {
                diff.append(diffMatchPatch.text);
            } else if (diffMatchPatch.operation == DiffMatchPatch.Operation.DELETE) {
                diff.append(DELETED_TAG.apply(true)).append(diffMatchPatch.text).append(DELETED_TAG.apply(false));
            } else if (diffMatchPatch.operation == DiffMatchPatch.Operation.INSERT) {
                diff.append(INSERTED_TAG.apply(true)).append(diffMatchPatch.text).append(INSERTED_TAG.apply(false));
            }
        }

        return diff.toString();
    }
}
