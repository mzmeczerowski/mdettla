#!/usr/bin/env python
# -*- coding: UTF-8 -*-

"""Ustawia importy w odpowiedniej kolejności w plikach źródłowych Javy."""

import re
import sys

USAGE = 'Użycie: ' + sys.argv[0] + ' PLIKI_JAVA...'


def organize_imports(java_file):

    def sort_imports(import_lines):

        def get_top_package(import_line):
            return re.search(import_regex, import_line).group(1)

        def compare_import_lines(left, right):
            if 'static' in left and 'static' not in right:
                return -1
            elif 'static' not in left and 'static' in right:
                return 1
            else:
                return -1 if left < right else (1 if right < left else 0)

        def add_empty_lines(sorted_import_lines):
            with_empty_lines = []
            prev_line = None
            for line in sorted_imports_lines:
                if prev_line is not None and \
                        get_top_package(prev_line) != get_top_package(line):
                    with_empty_lines.append('\n')
                with_empty_lines.append(line)
                prev_line = line
            return with_empty_lines

        remove_empty_lines = lambda lines: filter(lambda l: l.strip(), lines)
        sorted_imports_lines = list(set(remove_empty_lines(import_lines)))
        sorted_imports_lines.sort(cmp = compare_import_lines)
        return add_empty_lines(sorted_imports_lines)

    import_regex = r'^import (.*?)\.(.*?)$'
    output_java = ''
    import_lines = []
    is_inside_imports = False
    for line in java_file.readlines():
        if re.search(import_regex, line):
            is_inside_imports = True
        if is_inside_imports and line.strip() and \
                not re.search(import_regex, line):
            is_inside_imports = False
            output_java += ''.join(sort_imports(import_lines))
            output_java += '\n'
        if is_inside_imports:
            import_lines.append(line)
        else:
            output_java += line
    return output_java


if __name__ == '__main__':
    if len(sys.argv) < 2:
        sys.exit(USAGE)
    for filename in sys.argv[1:]:
        java_file = open(filename, 'rw+')

        output_java = organize_imports(java_file)

        java_file.seek(0)
        java_file.truncate(0)
        java_file.write(output_java)
        java_file.close()

