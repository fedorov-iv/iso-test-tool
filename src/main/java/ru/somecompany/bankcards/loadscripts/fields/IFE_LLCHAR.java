package ru.somecompany.bankcards.loadscripts.fields;

import org.jpos.iso.AsciiPrefixer;
import org.jpos.iso.BcdPrefixer;
import org.jpos.iso.BinaryPrefixer;
import org.jpos.iso.EbcdicInterpreter;
import org.jpos.iso.EbcdicPrefixer;
import org.jpos.iso.ISOStringFieldPackager;
import org.jpos.iso.RightTPadder;

/**
 * ISOFieldPackager CHARACTERS (ASCII & BINARY)
 * EBCDIC version of IF_CHAR
 * @author apr@cs.com.uy
 * @version $Id$
 * @see IF_CHAR
 * @see ISOComponent
 */
public class IFE_LLCHAR extends ISOStringFieldPackager {
    /** Used for the GenericPackager. */
    public IFE_LLCHAR() {
        super(0, null, RightTPadder.SPACE_PADDER, EbcdicInterpreter.INSTANCE, BinaryPrefixer.B);
    }

    /**
     * @param len - field len
     * @param description symbolic descrption
     */
    public IFE_LLCHAR(int len, String description) {
        super(len, description, RightTPadder.SPACE_PADDER, EbcdicInterpreter.INSTANCE, BinaryPrefixer.B);
    }
}

