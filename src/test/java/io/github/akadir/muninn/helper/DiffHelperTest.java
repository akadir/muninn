package io.github.akadir.muninn.helper;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author akadir
 * Date: 21.05.2020
 * Time: 20:07
 */
public class DiffHelperTest {

    @Test
    public void generateDiffGoogle() {
        String diff = DiffHelper.generateDiffGoogle("Hello World", "Goodbye World");
        Assert.assertEquals("<strike>Hello</strike><u><b>Goodbye</b></u> World", diff);

        diff = DiffHelper.generateDiffGoogle("In GOD We Trust ❤️", "InGODweTRUST❤️");
        Assert.assertEquals("In<strike> </strike>GOD<strike> We Trust </strike><u><b>weTRUST</b></u>❤️", diff);
    }

}