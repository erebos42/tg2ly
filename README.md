tg2ly
=====

Hackish tool to convert multiple tuxguitar files to a lilypond songbook.

The lilypond exporter of tuxguitar is only available from the gui and can only convert one file at a time. This tool takes some of the tuxguitar code and converts multiple tuxguitar files to a single lilypond file (or multiple combine via include). tg2ly is a pretty hackish solution that is tailored to my needs, so this is probably not useful for anybody else. If you still dare look into the source code, behold: it's ugly as hell!


Usage
-----
tg2ly [--version|-v] [--help|-h] [--force|-f] --in [in_path] --out [out_path]

The in_path has to be a directory with tuxguitar files. 

