package org.ziglang.eclipse;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ZigTokenizer extends RuleBasedScanner
{
    private static final Token COMMENT_TOKEN;
    private static final Token STRING_TOKEN;
    private static final Token KEYWORD_TOKEN;
    private static final Token BUILTIN_TOKEN;
    private static final Token IDENTIFIER_TOKEN;
    static {
        Display display = Display.getCurrent();
        COMMENT_TOKEN = new Token(new TextAttribute(new Color(display, 0x3f, 0x7f, 0x5f, 0xff)));
        STRING_TOKEN = new Token(new TextAttribute(new Color(display, 0x2c, 0x04, 0xff, 0xff)));
        KEYWORD_TOKEN = new Token(new TextAttribute(new Color(display, 0x93, 0x05, 0x55, 0xff), null, SWT.BOLD));
        BUILTIN_TOKEN = new Token(new TextAttribute(new Color(display, 0x93, 0x05, 0x55, 0xff)));
        IDENTIFIER_TOKEN = new Token(new TextAttribute(new Color(display, 0x00, 0x00, 0x00, 0xff)));
    }

    public ZigTokenizer()
    {
        WordRule keywordRule = new WordRule(new IWordDetector() {
            @Override
            public boolean isWordStart(char c)
            {
                c -= '0';
                if (c >= wordCharacterMap.length)
                    return false;
                return wordCharacterMap[c];
            }
            @Override
            public boolean isWordPart(char c)
            {
                return isWordStart(c);
            }
        }, IDENTIFIER_TOKEN);

        for (String text : new String[] { //
                "const", //
                "var", //
                "extern", //
                "packed", //
                "export", //
                "pub", //
                "noalias", //
                "inline", //
                "comptime", //
                "nakedcc", //
                "stdcallcc", //
                "volatile", //
                "align", //
                "section", //
                "struct", //
                "enum", //
                "union", //
                "break", //
                "return", //
                "continue", //
                "asm", //
                "defer", //
                "errdefer", //
                "unreachable", //
                "try", //
                "catch", //
                "async", //
                "await", //
                "suspend", //
                "resume", //
                "cancel", //
                "if", //
                "else", //
                "switch", //
                "and", //
                "or", //
                "while", //
                "for", //
                "fn", //
                "use", //
                "test", //
        }) {
            keywordRule.addWord(text, KEYWORD_TOKEN);
        }
        for (String text : new String[] { //
                "null", //
                "undefined", //
                "this", //
                "bool", //
                "f32", //
                "f64", //
                "f128", //
                "void", //
                "noreturn", //
                "type", //
                "error", //
                "promise", //
                "i2", //
                "u2", //
                "i3", //
                "u3", //
                "i4", //
                "u4", //
                "i5", //
                "u5", //
                "i6", //
                "u6", //
                "i7", //
                "u7", //
                "i8", //
                "u8", //
                "i16", //
                "u16", //
                "u29", //
                "i29", //
                "i32", //
                "u32", //
                "i64", //
                "u64", //
                "i128", //
                "u128", //
                "isize", //
                "usize", //
                "c_short", //
                "c_ushort", //
                "c_int", //
                "c_uint", //
                "c_long", //
                "c_ulong", //
                "c_longlong", //
                "c_ulonglong", //
                "c_longdouble", //
                "c_void", //
                "true", //
                "false", //
                "@addWithOverflow", //
                "@ArgType", //
                "@atomicLoad", //
                "@bitCast", //
                "@breakpoint", //
                "@alignCast", //
                "@alignOf", //
                "@cDefine", //
                "@cImport", //
                "@cInclude", //
                "@cUndef", //
                "@canImplicitCast", //
                "@clz", //
                "@cmpxchgWeak", //
                "@cmpxchgStrong", //
                "@compileError", //
                "@compileLog", //
                "@ctz", //
                "@divExact", //
                "@divFloor", //
                "@divTrunc", //
                "@embedFile", //
                "@export", //
                "@tagName", //
                "@TagType", //
                "@errorName", //
                "@errorReturnTrace", //
                "@fence", //
                "@fieldParentPtr", //
                "@frameAddress", //
                "@import", //
                "@inlineCall", //
                "@intToPtr", //
                "@IntType", //
                "@maxValue", //
                "@memberCount", //
                "@memberName", //
                "@memberType", //
                "@memcpy", //
                "@memset", //
                "@minValue", //
                "@mod", //
                "@mulWithOverflow", //
                "@noInlineCall", //
                "@offsetOf", //
                "@OpaqueType", //
                "@panic", //
                "@ptrCast", //
                "@ptrToInt", //
                "@rem", //
                "@returnAddress", //
                "@setCold", //
                "@setRuntimeSafety", //
                "@setEvalBranchQuota", //
                "@setFloatMode", //
                "@setGlobalLinkage", //
                "@setGlobalSection", //
                "@shlExact", //
                "@shlWithOverflow", //
                "@shrExact", //
                "@sizeOf", //
                "@sqrt", //
                "@subWithOverflow", //
                "@truncate", //
                "@typeId", //
                "@typeName", //
                "@typeOf", //
                "@atomicRmw", //
        }) {
            keywordRule.addWord(text, BUILTIN_TOKEN);
        }

        WhitespaceRule whitespaceRule = new WhitespaceRule(new IWhitespaceDetector() {
            @Override
            public boolean isWhitespace(char c)
            {
                return c == ' ' || c == '\n';
            }
        });
        setRules(new IRule[] { //
                keywordRule, //
                new SingleLineRule("//", null, COMMENT_TOKEN, (char)0, true, true), //
                new SingleLineRule("\\\\", null, STRING_TOKEN, (char)0, true, true), //
                new SingleLineRule("\"", "\"", STRING_TOKEN, '\\', true, true), //
                new SingleLineRule("'", "'", STRING_TOKEN, '\\', true, true), //
                whitespaceRule, //
        });
    }

    private static boolean[] wordCharacterMap = { //
            true, // '0'
            true, // '1'
            true, // '2'
            true, // '3'
            true, // '4'
            true, // '5'
            true, // '6'
            true, // '7'
            true, // '8'
            true, // '9'
            false, // ':'
            false, // ';'
            false, // '<'
            false, // '='
            false, // '>'
            false, // '?'
            true, // '@'
            true, // 'A'
            true, // 'B'
            true, // 'C'
            true, // 'D'
            true, // 'E'
            true, // 'F'
            true, // 'G'
            true, // 'H'
            true, // 'I'
            true, // 'J'
            true, // 'K'
            true, // 'L'
            true, // 'M'
            true, // 'N'
            true, // 'O'
            true, // 'P'
            true, // 'Q'
            true, // 'R'
            true, // 'S'
            true, // 'T'
            true, // 'U'
            true, // 'V'
            true, // 'W'
            true, // 'X'
            true, // 'Y'
            true, // 'Z'
            false, // '['
            false, // '\\'
            false, // ']'
            false, // '^'
            true, // '_'
            false, // '`'
            true, // 'a'
            true, // 'b'
            true, // 'c'
            true, // 'd'
            true, // 'e'
            true, // 'f'
            true, // 'g'
            true, // 'h'
            true, // 'i'
            true, // 'j'
            true, // 'k'
            true, // 'l'
            true, // 'm'
            true, // 'n'
            true, // 'o'
            true, // 'p'
            true, // 'q'
            true, // 'r'
            true, // 's'
            true, // 't'
            true, // 'u'
            true, // 'v'
            true, // 'w'
            true, // 'x'
            true, // 'y'
            true, // 'z'
    };
}
