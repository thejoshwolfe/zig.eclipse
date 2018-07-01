#!/usr/bin/env python

import re
import os

def main():
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument("zig_vim", metavar="zig.vim")
    args = parser.parse_args()

    with open(args.zig_vim) as f:
        contents = f.read()

    keywords = []
    builtins = []

    for group_name, tokens_str in re.findall(r'syn keyword (\w+) (.*)', contents):
        tokens = tokens_str.split()
        if tokens[0] == "contained": continue
        {
            "zigStorage": keywords,
            "zigStructure": keywords,
            "zigStatement": keywords,
            "zigConditional": keywords,
            "zigRepeat": keywords,
            "zigConstant": builtins,
            "zigKeyword": keywords,
            "zigType": builtins,
            "zigBoolean": builtins,
        }[group_name].extend(tokens)

    for at_builtins_str in re.findall(r'syn match zigBuiltinFn "\\v\\@\((.*)\)>"', contents):
        at_builtins = at_builtins_str.split("|")
        builtins.extend("@" + at_builtin for at_builtin in at_builtins)

    keywords.sort()
    builtins.sort()

    with open(os.path.join(os.path.dirname(__file__), *output_file_path.split("/")), "w") as f:
        f.write(output_file_template % {
            "keywords": "\n".join(output_line_template % word for word in keywords),
            "builtins": "\n".join(output_line_template % word for word in builtins),
        })

output_file_path = "workspace/org.ziglang.eclipse/src/org/ziglang/eclipse/ZigKeywordDefs.java"

output_file_template = """\
package org.ziglang.eclipse;

public interface ZigKeywordDefs
{
    public static final String[] KEYWORDS = { //
%(keywords)s
    };
    public static final String[] BUILTINS = { //
%(builtins)s
    };
}
"""

output_line_template = """\
            "%s", //
""".rstrip()

if __name__ == "__main__":
    main()

